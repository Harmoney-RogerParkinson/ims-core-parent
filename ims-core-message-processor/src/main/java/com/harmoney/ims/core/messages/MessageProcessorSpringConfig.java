package com.harmoney.ims.core.messages;

import static com.salesforce.emp.connector.LoginHelper.login;

import java.net.URL;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import com.salesforce.emp.connector.BayeuxParameters;
import com.salesforce.emp.connector.EmpConnector;
import com.salesforce.emp.connector.TopicSubscription;
import com.sforce.soap.enterprise.Connector;
import com.sforce.soap.enterprise.EnterpriseConnection;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

/**
 * This is a Spring Configuration class that defines the beans needed for the
 * database.
 * 
 * @author Roger Parkinson
 *
 */
@Configuration
@ComponentScan("com.harmoney.ims.core.messages")
@PropertySource(value={"classpath:test.properties"},ignoreResourceNotFound = true)
public class MessageProcessorSpringConfig {

	@Value("${salesforce.url}")
	public String salesforceURL;
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
	
    private static final Logger log = LoggerFactory.getLogger(MessageProcessorSpringConfig.class);
	@Autowired private MessageHandler messageHandler;

	// needed for @PropertySource
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertyConfigInProd() {
		PropertySourcesPlaceholderConfigurer ret = new PropertySourcesPlaceholderConfigurer();
		return ret;
	}

	@Bean(destroyMethod="stop")
	public EmpConnector empConnector() {

		BayeuxParameters params = null;
		try {
			log.debug("starting login: {} {}",salesforceURL,username);
			params = login(new URL(salesforceURL), username, password);
			log.debug("login successful: {} {}",salesforceURL,username);
		} catch (Exception e) {
			throw new MessageHandlerException("failed to login",e);
		}
		Consumer<Map<String, Object>> consumer = event -> {
			messageHandler.processMessage(event);
		};
		EmpConnector connector = new EmpConnector(params);

		try {
			connector.start().get(timeout, TimeUnit.SECONDS);
			log.debug("connector started");
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			throw new MessageHandlerException("failed to start", e);
		}

		TopicSubscription subscription;
		try {
			subscription = connector.subscribe(topic, replayFrom,
					consumer).get(5, TimeUnit.SECONDS);
			log.debug("subscription created: {} replayFrom {}",topic, replayFrom);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			throw new MessageHandlerException("failed to subscribe",e);
		}
		return connector;
	}
	@Bean(destroyMethod="logout")
	public EnterpriseConnection enterpriseConnection() throws ConnectionException {

        EnterpriseConnection connection = null;
		ConnectorConfig config = new ConnectorConfig();
		config.setUsername(username);
		config.setPassword(password+securityToken);
		connection = Connector.newConnection(config);

		// display some current settings
		log.info("Auth EndPoint:{}",config.getAuthEndpoint());
		log.info("Service EndPoint: {}", config.getServiceEndpoint());
		log.info("Username: {}", config.getUsername());
		log.info("SessionId: {}", config.getSessionId());
		log.info("Service endpoint: {}",config.getServiceEndpoint());
		return connection;
	}

}
