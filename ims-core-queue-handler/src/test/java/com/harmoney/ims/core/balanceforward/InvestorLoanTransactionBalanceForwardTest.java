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

	@Test
	public void processBalanceForward() throws Exception {
		if (StringUtils.hasText(hbm2ddlauto) && hbm2ddlauto.startsWith("create")) {
			// only load database if we just created it
			// otherwise assume a preloaded one
			loadDatabase();
		}
		
		LocalDate period1 = LocalDate.of(2016, 7, 15);
		LocalDate period2 = LocalDate.of(2016, 8, 15);
		LocalDate period3 = LocalDate.of(2016, 9, 15);

		Map<String,BigDecimal> netAmountByAccountIdPeriod1 = new HashMap<>();
		Map<String,BigDecimal> netAmountByAccountIdPeriod2 = new HashMap<>();
		Map<String,BigDecimal> netAmountByAccountIdPeriod3 = new HashMap<>();

		// Run the first period. Should be three account ids and no existing balfwds, so we create three.
		processPeriod(period1, netAmountByAccountIdPeriod1, true);
		// Run the first period again. Should update the three balfwds with the same amount, not create new ones.
		processPeriod(period1, netAmountByAccountIdPeriod1, false);
		
		// now run the next period. That should result 5 account ids.
		// The three previous ones have no txns so we just create a balfwd in each of them with the same total as the prev period
		// The two new ones create one new balfwd each
		processPeriod(period2, netAmountByAccountIdPeriod2, true);
		// rerun period 2 and verify the balance forwards and totals are the same
		processPeriod(period2,  netAmountByAccountIdPeriod2, false);

		processPeriod(period3, netAmountByAccountIdPeriod3, true);
		// rerun period 2 and verify the balance forwards and totals are the same
		processPeriod(period3,  netAmountByAccountIdPeriod3, false);

		// Rerun all the periods (again)
		// verify the balfwd counts and the final totals do not change
		processPeriod(period1, netAmountByAccountIdPeriod1, false);
		processPeriod(period2, netAmountByAccountIdPeriod2, false);
		processPeriod(period3, netAmountByAccountIdPeriod3, false);
	}
	
	/**
	 * Runs the balance forward processing for the period contained by the date given.
	 * The object returned is a summary of the accounts processed and their balfwd totals
	 * (the balfwd totals is suppressed unless we are in test mode).
	 * Analyse that the counts of balfwd records found on the database matches the number the process thing=ks there are
	 * and, depending on our mode (put is true or false) store the current final balfwd value or compare the
	 * final value with the one previously put.
	 * 
	 * @param periodDate
	 * @param netAmountByAccountId
	 * @param put
	 */
	private void processPeriod(LocalDate periodDate, Map<String,BigDecimal> netAmountByAccountId, boolean put) {
		
		log.debug("*** Processing Period {} {}",periodDate,put);

		BalanceForwardDTO balanceForwardDTO = investorLoanTransactionBalanceForward.processBalanceForward(periodDate);

		for (String accountId: balanceForwardDTO.getAccountIds()) {
			List<InvestorLoanTransaction> balfwdlist = investorLoanTransactionDAO.getByAccountDateBalFwd(
					balanceForwardDTO.getStart(),
					balanceForwardDTO.getEnd(),
					accountId
					);
			int s = balfwdlist.size();
			assertEquals(balanceForwardDTO.get(accountId),s);
			if (put) {
				netAmountByAccountId.put(accountId, balfwdlist.get(s-1).getNetAmount());
			} else {
				assertEquals(netAmountByAccountId.get(accountId),balfwdlist.get(s-1).getNetAmount());
			}
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
