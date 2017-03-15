package com.harmoney.ims.core.balanceforward;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlProducer;
import org.dbunit.ext.h2.H2DataTypeFactory;
import org.dbunit.ext.postgresql.PostgresqlDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.StringUtils;
import org.xml.sax.InputSource;

import com.harmoney.ims.core.database.DatabaseSpringConfig;
import com.harmoney.ims.core.database.InvestorLoanTransactionDAO;
import com.harmoney.ims.core.instances.InvestorLoanTransaction;
import com.harmoney.ims.core.partner.PartnerConnectionSpringConfig;
import com.harmoney.ims.core.queries.QuerySpringConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource("/BalanceForwardTest.properties")
@ContextConfiguration(classes = { QuerySpringConfig.class, PartnerConnectionSpringConfig.class,DatabaseSpringConfig.class})
public class InvestorLoanTransactionBalanceForwardTest {

    private static final Logger log = LoggerFactory.getLogger(InvestorLoanTransactionBalanceForwardTest.class);

	@Value("${database.hbm2ddl.auto}")
	private String hbm2ddlauto;
	@Value("${database.dialect}")
	private String databaseDialect;
	@Autowired DataSource dataSource;
	@Autowired InvestorLoanTransactionBalanceForward investorLoanTransactionBalanceForward;
	@Autowired InvestorLoanTransactionDAO investorLoanTransactionDAO;
    private static final String dbLocation = "ims.xml";

	@Test @Ignore // still testing
	public void processBalanceForward() throws Exception {
		if (StringUtils.hasText(hbm2ddlauto) && hbm2ddlauto.startsWith("create")) {
			// only load database if we just created it
			// otherwise assume a preloaded one
			loadDatabase();
		}
		// period 2016-07 has two accounts to process
		BalanceForwardDTO balanceForwardDTO = investorLoanTransactionBalanceForward.processBalanceForward(LocalDate.of(2016, 7, 15));

		Map<String,BigDecimal> netAmountByAccountId = new HashMap<>(); 
		// Query for the new balfwd records, verify the number, save the amounts.
		for (String accountId: balanceForwardDTO.getAccountIds()) {
			List<InvestorLoanTransaction> balfwdlist = investorLoanTransactionDAO.getByAccountDateBalFwd(
					balanceForwardDTO.getStart(),
					balanceForwardDTO.getEnd(),
					accountId
					);
			assertEquals(1,balfwdlist.size());
			netAmountByAccountId.put(accountId, balfwdlist.get(0).getNetAmount());
		}
		// Rerun the same process for the same dates
		// Should update the existing records with the same values (ie process is repeatable) 
		balanceForwardDTO = investorLoanTransactionBalanceForward.processBalanceForward(LocalDate.of(2016, 7, 15));
		// Query for the new balfwd records, verify the number and the amounts.
		for (String accountId: balanceForwardDTO.getAccountIds()) {
			List<InvestorLoanTransaction> balfwdlist = investorLoanTransactionDAO.getByAccountDateBalFwd(
					balanceForwardDTO.getStart(),
					balanceForwardDTO.getEnd(),
					accountId
					);
			assertEquals(1,balfwdlist.size());
			assertEquals(netAmountByAccountId.get(accountId),balfwdlist.get(0).getNetAmount());
		}
		
		// now run the next period. That should result in two balfwd records under each of the accounts
		Map<String,BigDecimal> netAmountByAccountId2 = new HashMap<>(); 
		BalanceForwardDTO balanceForwardDTO2 = investorLoanTransactionBalanceForward.processBalanceForward(LocalDate.of(2016, 8, 15));
		// Query for the new balfwd records, verify the number, save the amounts.
		for (String accountId: balanceForwardDTO.getAccountIds()) {
			List<InvestorLoanTransaction> balfwdlist = investorLoanTransactionDAO.getByAccountDateBalFwd(
					balanceForwardDTO.getStart(),
					balanceForwardDTO.getEnd(),
					accountId
					);
			assertEquals(2,balfwdlist.size());
			netAmountByAccountId2.put(accountId, balfwdlist.get(1).getNetAmount());
		}
		// Rerun the same process for the same dates (ie second period)
		// Should update the existing records with the same values (ie process is repeatable) 
		balanceForwardDTO2 = investorLoanTransactionBalanceForward.processBalanceForward(LocalDate.of(2016, 8, 15));
		// Query for the new balfwd records, verify the number and the amounts.
		for (String accountId: balanceForwardDTO.getAccountIds()) {
			List<InvestorLoanTransaction> balfwdlist = investorLoanTransactionDAO.getByAccountDateBalFwd(
					balanceForwardDTO.getStart(),
					balanceForwardDTO.getEnd(),
					accountId
					);
			assertEquals(2,balfwdlist.size());
			assertEquals(netAmountByAccountId2.get(accountId),balfwdlist.get(1).getNetAmount());
		}
		// Rerun the same process for the first period (again)
		// Should update the existing records with the same values (ie process is repeatable) 
		balanceForwardDTO = investorLoanTransactionBalanceForward.processBalanceForward(LocalDate.of(2016, 7, 15));
		// Query for the new balfwd records, verify the number and the amounts.
		for (String accountId: balanceForwardDTO.getAccountIds()) {
			List<InvestorLoanTransaction> balfwdlist = investorLoanTransactionDAO.getByAccountDateBalFwd(
					balanceForwardDTO.getStart(),
					balanceForwardDTO.getEnd(),
					accountId
					);
			assertEquals(1,balfwdlist.size());
			assertEquals(netAmountByAccountId.get(accountId),balfwdlist.get(0).getNetAmount());
		}
		
		
		
	}

	/**
	 * Loads the database from the extract xml file
	 * 
	 * @throws Exception
	 */
	private void loadDatabase() throws Exception {
		log.debug("Loading database hbm2ddlauto={} database dialect {}",hbm2ddlauto,databaseDialect);
		Connection jdbcConnection = dataSource.getConnection();
		IDatabaseConnection connection = new DatabaseConnection(jdbcConnection);
		DatabaseConfig dbConfig = connection.getConfig();
		dbConfig.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new H2DataTypeFactory());
        if (databaseDialect.equals("org.hibernate.dialect.PostgreSQLDialect")) {
    		dbConfig.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new PostgresqlDataTypeFactory());
        }
        FlatXmlDataSet dataSet = new FlatXmlDataSet(new FlatXmlProducer(new InputSource(new FileInputStream(dbLocation)),false,true,true));
        DatabaseOperation.CLEAN_INSERT.execute(connection, dataSet);
        if (databaseDialect.equals("org.hibernate.dialect.PostgreSQLDialect")) {
        // The database load fails to update the hibernate sequence so we force it here.
        // Current file take it to just over 3000 so 4000 allows for some padding
	        CallableStatement callable = jdbcConnection.prepareCall("ALTER SEQUENCE hibernate_sequence RESTART WITH 4000;");
	        callable.execute();
        }
        log.debug("Database load complete {}",databaseDialect);
        
	}
}
