package com.harmoney.ims.core.databaseloader;

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
import org.dbunit.ext.h2.H2DataTypeFactory;
import org.dbunit.ext.postgresql.PostgresqlDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xml.sax.InputSource;

import com.harmoney.ims.core.balanceforward.InvestorFundTransactionBalanceForward;
import com.harmoney.ims.core.balanceforward.InvestorLoanTransactionBalanceForward;
import com.harmoney.ims.core.database.ConfiguredDatabaseParameters;

@Component
public class DatabaseLoader {
	
    private static final Logger log = LoggerFactory.getLogger(DatabaseLoader.class);

	@Autowired ConfiguredDatabaseParameters configuredParameters;
	@Autowired DataSource dataSource;
	@Autowired InvestorLoanTransactionBalanceForward investorLoanTransactionBalanceForward;
	@Autowired InvestorFundTransactionBalanceForward investorFundTransactionBalanceForward;


	/**
	 * Loads the database from the extract xml file
	 * 
	 * @throws Exception
	 */
	public void loadDatabase(String dbLocation) throws Exception {
		if (configuredParameters.isHbm2ddlautoCreate()) {
			// only load database if we just created it
			// otherwise assume a preloaded one
			log.debug("Loading database hbm2ddlauto={} database dialect {}",
					configuredParameters.getHbm2ddlAuto(),configuredParameters.getDialect());
			Connection jdbcConnection = dataSource.getConnection();
			IDatabaseConnection connection = new DatabaseConnection(jdbcConnection);
			DatabaseConfig dbConfig = connection.getConfig();
			dbConfig.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new H2DataTypeFactory());
	        if (configuredParameters.getDialect().equals("org.hibernate.dialect.PostgreSQLDialect")) {
	    		dbConfig.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new PostgresqlDataTypeFactory());
	        }
	        FlatXmlDataSet dataSet = new FlatXmlDataSet(new FlatXmlProducer(new InputSource(new FileInputStream(dbLocation)),false,true,true));
	        DatabaseOperation.CLEAN_INSERT.execute(connection, dataSet);
	        if (configuredParameters.getDialect().equals("org.hibernate.dialect.PostgreSQLDialect")) {
	        // The database load fails to update the hibernate sequence so we force it here.
	        // Current file take it to just over 3000 so 4000 allows for some padding
		        CallableStatement callable = jdbcConnection.prepareCall("ALTER SEQUENCE hibernate_sequence RESTART WITH 4000;");
		        callable.execute();
	        }
	        log.debug("Database load complete {}\n",configuredParameters.getDialect());
		} else {
			log.debug("Database load suppressed (not started with hbm2ddlauto=create*)");
		}
	}
	
	public void calculateBalanceForward() {
		LocalDate period1 = LocalDate.of(2017, 2, 15);
		LocalDate period2 = LocalDate.of(2017, 3, 15);

		investorLoanTransactionBalanceForward.processBalanceForward(period1);
		investorFundTransactionBalanceForward.processBalanceForward(period1);
		investorLoanTransactionBalanceForward.processBalanceForward(period2);
		investorFundTransactionBalanceForward.processBalanceForward(period2);
		
	}
}
