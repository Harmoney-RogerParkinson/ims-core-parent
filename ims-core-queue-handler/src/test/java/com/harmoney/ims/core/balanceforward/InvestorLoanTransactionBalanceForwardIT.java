package com.harmoney.ims.core.balanceforward;

import java.io.FileInputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.time.LocalDate;

import javax.sql.DataSource;

import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlProducer;
import org.dbunit.ext.postgresql.PostgresqlDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.StringUtils;
import org.xml.sax.InputSource;

import com.harmoney.ims.core.database.DatabaseSpringConfig;
import com.harmoney.ims.core.partner.PartnerConnectionSpringConfig;
import com.harmoney.ims.core.queries.QuerySpringConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { QuerySpringConfig.class, PartnerConnectionSpringConfig.class,DatabaseSpringConfig.class})
public class InvestorLoanTransactionBalanceForwardIT {

	 @Value("${database.hbm2ddl.auto}")
	 public String hbm2ddlauto;
	@Autowired DataSource dataSource;
	@Autowired InvestorLoanTransactionBalanceForward investorLoanTransactionBalanceForward;
    private static final String dbLocation = "/tmp/ims.xml";

	@Test
	public void processBalanceForward() throws Exception {
		if (StringUtils.hasText(hbm2ddlauto) && hbm2ddlauto.startsWith("create")) {
			// only load database if we just created it
			// otherwise assume a preloaded one
			loadDatabase();
		}
		// period 2016-08 has two accounts to process
		investorLoanTransactionBalanceForward.processBalanceForward(LocalDate.of(2016, 8, 15));
		
		
	}

	/**
	 * Loads the database from the extract xml file
	 * 
	 * @throws Exception
	 */
	private void loadDatabase() throws Exception {
		Connection jdbcConnection = dataSource.getConnection();
		IDatabaseConnection connection = new DatabaseConnection(jdbcConnection);
		DatabaseConfig dbConfig = connection.getConfig();
		dbConfig.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new PostgresqlDataTypeFactory());
		
        FlatXmlDataSet dataSet = new FlatXmlDataSet(new FlatXmlProducer(new InputSource(new FileInputStream(dbLocation)),false,true,true));
        DatabaseOperation.CLEAN_INSERT.execute(connection, dataSet);
        // The database load fails to update the hibernate sequence so we force it here.
        // Current file take it to just over 3000 so 4000 allows for some padding
        CallableStatement callable = jdbcConnection.prepareCall("ALTER SEQUENCE hibernate_sequence RESTART WITH 4000;");
        callable.execute();
        
	}
}
