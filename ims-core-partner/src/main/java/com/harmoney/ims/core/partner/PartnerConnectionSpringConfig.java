package com.harmoney.ims.core.partner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import com.sforce.soap.partner.PartnerConnection;
import com.sforce.ws.ConnectionException;
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

	@Bean(destroyMethod="logout")
	public PartnerConnection partnerConnection() throws ConnectionException {

		PartnerConnection connection = null;
		ConnectorConfig config = new ConnectorConfig();
		config.setUsername(configuredParameters.getUsername());
		config.setPassword(configuredParameters.getPassword());
		config.setAuthEndpoint(configuredParameters.getAuthEndpoint());
		connection = new PartnerConnection(config);

		// display some current settings
		log.info("Auth EndPoint:{}",config.getAuthEndpoint());
		log.info("Service EndPoint: {}", config.getServiceEndpoint());
		log.info("Username: {}", config.getUsername());
		log.info("SessionId: {}", config.getSessionId());
		log.info("Service endpoint: {}",config.getServiceEndpoint());
		return connection;
	}

}
