package com.harmoney.ims.core.enterprise;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import com.sforce.soap.enterprise.EnterpriseConnection;
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
public class EnterpriseConnectionSpringConfig {

	@Value("${salesforce.url}")
	public String salesforceURL;
	@Value("${salesforce.authEndpoint}")
	public String authEndpoint;
	@Value("${salesforce.username}")
	public String username;
	@Value("${salesforce.password}")
	public String password;
	@Value("${salesforce.security.token}")
	public String securityToken;
	@Value("${salesforce.topic}")
	public String topic;
	@Value("${salesforce.replayFrom:-1}")
	public long replayFrom;
	@Value("${salesforce.timeout:5}")
	public long timeout;
	
    private static final Logger log = LoggerFactory.getLogger(EnterpriseConnectionSpringConfig.class);

	// needed for @PropertySource
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertyConfigInProd() {
		PropertySourcesPlaceholderConfigurer ret = new PropertySourcesPlaceholderConfigurer();
		return ret;
	}

	@Bean(destroyMethod="logout")
	public EnterpriseConnection partnerConnection() throws ConnectionException {

		EnterpriseConnection connection = null;
		ConnectorConfig config = new ConnectorConfig();
		config.setUsername(username);
		config.setPassword(password);
		config.setAuthEndpoint(authEndpoint);
		connection = new EnterpriseConnection(config);

		// display some current settings
		log.info("Auth EndPoint:{}",config.getAuthEndpoint());
		log.info("Service EndPoint: {}", config.getServiceEndpoint());
		log.info("Username: {}", config.getUsername());
		log.info("SessionId: {}", config.getSessionId());
		log.info("Service endpoint: {}",config.getServiceEndpoint());
		return connection;
	}

}
