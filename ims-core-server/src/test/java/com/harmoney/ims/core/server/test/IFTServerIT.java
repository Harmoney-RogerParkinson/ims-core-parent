package com.harmoney.ims.core.server.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.harmoney.ims.core.database.DatabaseSpringConfig;
import com.harmoney.ims.core.messages.MessageProcessorSpringConfig;
import com.harmoney.ims.core.partner.PartnerConnectionSpringConfig;
import com.harmoney.ims.core.queuehandler.QueueHandlerSpringConfig;
import com.harmoney.ims.core.queuehandler.ReceiverMock;
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
 * It uses the loan__Investor_Fund_Transaction__c table and updates loan__Reject_Reason__c to trigger the pushTopic.
 * 
 * Uses the intsb sandbox.
 * 
 * @author Roger Parkinson
 * 
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource("/test2.properties")
@ContextConfiguration(classes={MessageProcessorSpringConfig.class,PartnerConnectionSpringConfig.class,QueueHandlerSpringConfig.class,DatabaseSpringConfig.class})
@ActiveProfiles({"message-processor-prod","server-dev"})
public class IFTServerIT {
	
    private static final Logger log = LoggerFactory.getLogger(IFTServerIT.class);
	@Autowired private EmpConnector empConnector;
    @Autowired ReceiverMock receiver;
    @Autowired ConfigurableApplicationContext context;
	@Autowired private PartnerConnection partnerConnection;

	@Test
	public void testSubscription() throws ConnectionException, InterruptedException {
		assertNotNull(empConnector);
		updateInvestorLoanTransaction();
		"".toCharArray();
		assertTrue("Did not reach expected count",receiver.getLatch().await(10000, TimeUnit.MILLISECONDS));
	}

	private void updateInvestorLoanTransaction() throws ConnectionException {
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
			if (count++ > 5) {
				saved = saved + saveResults(updates);
				count = 0;
				updates.clear();
				break;
			}
		}
		if (count > 0) {
			saved = saved + saveResults(updates);
		}
		log.debug("records updated: {}",saved);
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
