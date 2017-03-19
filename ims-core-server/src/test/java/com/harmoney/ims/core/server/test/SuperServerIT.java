package com.harmoney.ims.core.server.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.ext.h2.H2DataTypeFactory;
import org.dbunit.ext.postgresql.PostgresqlDataTypeFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.StringUtils;

import com.harmoney.ims.core.database.DatabaseSpringConfig;
import com.harmoney.ims.core.messages.MessageProcessorSpringConfig;
import com.harmoney.ims.core.partner.PartnerConnectionSpringConfig;
import com.harmoney.ims.core.queries.AccountQuery;
import com.harmoney.ims.core.queries.InvestmentOrderQuery;
import com.harmoney.ims.core.queries.QuerySpringConfig;
import com.harmoney.ims.core.queuehandler.QueueHandlerSpringConfig;
import com.harmoney.ims.core.queuehandler.RabbitReceiver;
import com.salesforce.emp.connector.EmpConnector;
import com.sforce.soap.partner.Error;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.SaveResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;

/**
 * This is an integration test. It waits on a pushTopic and while it is waiting
 * sends a SOAP update to Salesforce so that a message will be sent to the topic.
 * The message will be processed/logged on another thread by the message handler.
 * Because the active profile is 'dev' the mock message handler will be injected rather
 * than the production 'prod' one. The mock message handler counts the message and interrupts
 * the main thread from its sleep.
 * 
 * It uses the loan__Investor_Loan_Account_Txns__c table and updates test__c to trigger the pushTopic.
 * 
 * Uses the intsb sandbox.
 * 
 * @author Roger Parkinson
 * 
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource("/SuperServerIT.properties")
@ContextConfiguration(classes={
		MessageProcessorSpringConfig.class,
		PartnerConnectionSpringConfig.class,
		QueueHandlerSpringConfig.class,
		DatabaseSpringConfig.class,
		QuerySpringConfig.class})
@ActiveProfiles({"message-processor-prod","server-prod","queue-handler-prod"})
public class SuperServerIT {
	
    private static final Logger log = LoggerFactory.getLogger(SuperServerIT.class);
    private static final int SAVE_BUFFER = 75;
    private static final int MAX_TRANSACTIONS = 500;
	@Autowired EmpConnector empConnector;
    @Autowired ConfigurableApplicationContext context;
	@Autowired PartnerConnection partnerConnection;
	@Autowired AccountQuery accountSummaryquery;
	@Autowired InvestmentOrderQuery investmentOrderquery;
	@Autowired DataSource dataSource;
	@Autowired RabbitReceiver rabbitReceiver;
	@Value("${com.harmoney.ims.core.server.test.SuperServerIT.transactionsOnly:false}")
	private boolean transactionsOnly;
	@Value("${com.harmoney.ims.core.server.test.SuperServerIT.saveData:/tmp/ims.xml}")
	private String dbLocation;
	@Value("${database.dialect}")
	private String databaseDialect;

	@Test
	public void testEverythingFromSalesforce() throws Exception {
		assertNotNull(empConnector);
		
		log.info("Transactions only: {}",transactionsOnly);
		log.info("Database: {}",databaseDialect);
		CountDownLatch latch = null;
		int iltCount = 0;
		int iftCount = 0;
		// There's a little sync dance going on here. The Rabbit receiver doesn't do any syncing
		// in production but here we want to prevent it from running ahead of us and then
		// getting behind us later. It only syncs when there is a latch set and we don't know how
		// big a latch to set until we've run the updates (by which time the pushtopics have started
		// arriving before we can count them). So we set a dummy latch at first and do the updates
		// while the rabbitReceiver is locked, making it wait to process any incoming records.
		// Once the updates are done we put in the real latch (with the correct count)
		// and then we can release the lock and wait for the count.
		rabbitReceiver.setLatch(new CountDownLatch(iltCount+iftCount));
		synchronized(rabbitReceiver) {
			log.info("Updating transactions...");
			iltCount = updateInvestorLoanTransaction(MAX_TRANSACTIONS);
			iftCount = updateInvestorFundTransaction(MAX_TRANSACTIONS);
			latch = new CountDownLatch(iltCount+iftCount);
			rabbitReceiver.setLatch(latch);
		}
		log.info("Waiting for {} pushTopic records...",latch.getCount());
		assertTrue("Did not reach expected count",latch.await(100000, TimeUnit.MILLISECONDS));
		log.info("rabbitReceiver processed: {} of {}",rabbitReceiver.getCount(),iltCount+iftCount);
		log.info("PushTopic updates complete");
		assertEquals(rabbitReceiver.getCount(),iltCount+iftCount);
		if (!transactionsOnly) {
			int accountSummaryCount = accountSummaryquery.doQuery();
			log.info("accountSummaryCount {}",accountSummaryCount);
			int investmentOrderCount = investmentOrderquery.doQuery();
			log.info("investmentOrderCount {}",investmentOrderCount);
		}
		if (StringUtils.hasText(dbLocation)) {
			saveDatabase(dbLocation);
			log.info("Database image saved to {}",dbLocation);
		} else {
			log.info("Database image save disabled");
		}
		shutownDatabase();
	}
	
	private void shutownDatabase() throws SQLException{
        if (databaseDialect.equals("org.hibernate.dialect.H2Dialect")) {
        	dataSource.getConnection().createStatement().execute("SHUTDOWN");
        }
	}
	
	private void saveDatabase(String outputFile) throws Exception {
		Connection jdbcCconnection = dataSource.getConnection();
		IDatabaseConnection connection = new DatabaseConnection(jdbcCconnection);
		DatabaseConfig dbConfig = connection.getConfig();
		dbConfig.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new H2DataTypeFactory());
        if (databaseDialect.equals("org.hibernate.dialect.PostgreSQLDialect")) {
    		dbConfig.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new PostgresqlDataTypeFactory());
        }
        IDataSet fullDataSet = connection.createDataSet();
        FlatXmlDataSet.write(fullDataSet, new FileOutputStream(outputFile));		
	}

	private int updateInvestorLoanTransaction(int max) throws ConnectionException {
		int saved = 0;
		String testValue = "RJP"+LocalDateTime.now().toLocalTime().toString();
		String queryString = "SELECT Id,test__c FROM loan__Investor_Loan_Account_Txns__c";
		if (!transactionsOnly) {
			queryString = "SELECT Id,test__c FROM loan__Investor_Loan_Account_Txns__c where CreatedDate <= 2016-12-31T00:00:00Z order by CreatedDate desc";
		}
		QueryResult qr = partnerConnection.query(queryString);
		qr.getSize();
		List<SObject> updates = new ArrayList<>();
		int count = 0;
		SObject[] records = qr.getRecords();
		for (SObject r: records) {
			String id = (String)r.getField("Id");
			String t = (String)r.getField("test__c");
			if (StringUtils.isEmpty(t) || t.startsWith("RJP")) {
				SObject r1 = new SObject();
				r1.setType("loan__Investor_Loan_Account_Txns__c");
				r1.setField("test__c", testValue);
				r1.setField("Id", id);
				updates.add(r1);
				if (count++ > SAVE_BUFFER) {
					saved = saved + saveResults(updates);
					log.info("ILT... {}",saved);
					count = 0;
					updates.clear();
					if (saved >= max) {
						break;
					}
				}
			}
		}
		if (count > 0 && !(saved >= max)) {
			saved = saved + saveResults(updates);
		}
		log.info("ILT records updated: {}",saved);
		return saved;
	}
	
	private int updateInvestorFundTransaction(int max) throws ConnectionException {
		int saved = 0;
		String testValue = "RJP"+LocalDateTime.now().toLocalTime().toString();
		QueryResult qr = partnerConnection.query("SELECT Id,LastModifiedDate FROM loan__Investor_Fund_Transaction__c");
		qr.getSize();
		List<SObject> updates = new ArrayList<>();
		int count = 0;
		SObject[] records = qr.getRecords();
		for (SObject r: records) {
			String id = (String)r.getField("Id");
			SObject r1 = new SObject();
			r1.setType("loan__Investor_Fund_Transaction__c");
			r1.setField("loan__Reject_Reason__c", testValue);
			r1.setField("Id", id);
			updates.add(r1);
			if (count++ > SAVE_BUFFER) {
				saved = saved + saveResults(updates);
				log.info("IFT... {}",saved);
				count = 0;
				updates.clear();
				if (saved >= max) {
					break;
				}
			}
			if (saved >= max) {
				break;
			}
		}
		if (count > 0 && !(saved >= max)) {
			saved = saved + saveResults(updates);
		}
		log.info("IFT records updated: {}",saved);
		return saved;
	}
	private int saveResults(List<SObject> records) throws ConnectionException {
		int saved = 0;
		SObject[] sobjects = records.toArray(new SObject[records.size()]);
		SaveResult[] saveResults = partnerConnection.update(sobjects);
		// check the returned results for any errors
		for (int i = 0; i < saveResults.length; i++) {
			if (saveResults[i].isSuccess()) {
				saved++;
				log.debug(i
						+ ". Successfully updated record - Id: "
						+ saveResults[i].getId());
			} else {
				Error[] errors = saveResults[i].getErrors();
				for (int j = 0; j < errors.length; j++) {
					log.debug("ERROR updating record: "
							+ errors[j].getMessage());
				}
			}
		}
		return saved;
		
	}
}
