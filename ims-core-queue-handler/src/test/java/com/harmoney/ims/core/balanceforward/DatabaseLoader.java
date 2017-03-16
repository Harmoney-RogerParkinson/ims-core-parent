package com.harmoney.ims.core.balanceforward;

import java.io.FileInputStream;
import java.sql.CallableStatement;
import java.sql.Connection;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.xml.sax.InputSource;

@Component
public class DatabaseLoader {
	
    private static final Logger log = LoggerFactory.getLogger(DatabaseLoader.class);

    @Value("${database.hbm2ddl.auto:}")
    private String hbm2ddlauto;
	@Value("${database.dialect:org.hibernate.dialect.PostgreSQLDialect}")
	private String databaseDialect;
	@Autowired DataSource dataSource;


	/**
	 * Loads the database from the extract xml file
	 * 
	 * @throws Exception
	 */
	public void loadDatabase(String dbLocation) throws Exception {
		if (StringUtils.hasText(hbm2ddlauto) && hbm2ddlauto.startsWith("create")) {
			// only load database if we just created it
			// otherwise assume a preloaded one
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
	        log.debug("Database load complete {}\n",databaseDialect);
		} else {
			log.debug("Database load suppressed (not started with hbm2ddlauto=create*)");
		}
			
        
	}
}
