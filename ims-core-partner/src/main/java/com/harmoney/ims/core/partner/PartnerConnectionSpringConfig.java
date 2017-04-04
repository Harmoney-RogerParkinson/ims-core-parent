package com.harmoney.ims.core.partner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import com.sforce.ws.ConnectorConfig;

/**
 * This is a Spring Configuration class that defines the beans needed for the
 * Enterprise connection, ie using the SOAP API to talk to the Salesforce database.
 * 
 * @author Roger Parkinson
 *
 */
@Configuration
@ComponentScan("com.harmoney.ims.core.partner")
public class PartnerConnectionSpringConfig {

	@Autowired ConfiguredSalesforceParameters configuredParameters;
	
    private static final Logger log = LoggerFactory.getLogger(PartnerConnectionSpringConfig.class);

	// needed for @PropertySource
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertyConfigInProd() {
		PropertySourcesPlaceholderConfigurer ret = new PropertySourcesPlaceholderConfigurer();
		return ret;
	}
	
//	@Bean ConnectorConfig connectorConfig() {
//		ConnectorConfig config = new ConnectorConfig();
//		config.setUsername(configuredParameters.getUsername());
//		config.setPassword(configuredParameters.getPassword()+configuredParameters.getSecurityToken());
//		config.setAuthEndpoint(configuredParameters.getAuthEndpoint());
//		log.info("Salesforce Partner Connection:\nAuth EndPoint: {}\nService EndPoint: {}\nUsername: {}\nSessionId: {}",
//				config.getAuthEndpoint(),config.getServiceEndpoint(),config.getUsername(),config.getSessionId());
//		return config;
//	}

//	@Bean(destroyMethod="logout")
//	public PartnerConnection partnerConnection() throws ConnectionException {
//
//		PartnerConnection connection = null;
//		log.info("Salesforce Connection...");
//		connection = new PartnerConnection(config);
//
//		// display some current settings
//		if (log.isDebugEnabled()) {
//			QueryResult qr = connection.query("SELECT Id,test__c FROM loan__Loan_Account__c where loan__Protect_Enabled__c = true");
//			log.debug("found {} records in sample query",qr.getSize());
//			QueryResult qr1 = connection.query("SELECT  Id,Name,loan__Loan_Account__c, loan__Transaction_Date__c FROM loan__Loan_account_Due_Details__c where Protect_Enabled__c = true");
//			log.debug("found {} records in sample query",qr1.getSize());
//		}
//
//		return connection;
//	}

}
