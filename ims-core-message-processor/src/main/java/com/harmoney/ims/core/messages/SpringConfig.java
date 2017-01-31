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
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;

import com.salesforce.emp.connector.BayeuxParameters;
import com.salesforce.emp.connector.EmpConnector;
import com.salesforce.emp.connector.TopicSubscription;

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
public class SpringConfig {

	@Value("${bayeux.url}")
	public String bayeuxURL;
	@Value("${bayeux.username}")
	public String username;
	@Value("${bayeux.password}")
	public String password;
	@Value("${bayeux.topic}")
	public String topic;
	@Value("${bayeux.replayFrom:-1}")
	public long replayFrom;
	@Value("${bayeux.timeout:5}")
	public long timeout;
	
    private static final Logger log = LoggerFactory.getLogger(SpringConfig.class);
	@Autowired private MessageHandler messageHandler;

	// needed for @PropertySource
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertyConfigInProd() {
		PropertySourcesPlaceholderConfigurer ret = new PropertySourcesPlaceholderConfigurer();
		return ret;
	}

	@Bean
	public TopicSubscription empConnector() {

		BayeuxParameters params = null;
		try {
			params = login(new URL(bayeuxURL), username, password);
		} catch (Exception e) {
			throw new MessageHandlerException("failed to login",e);
		}
		Consumer<Map<String, Object>> consumer = event -> {
			messageHandler.processMessage(event);
		};
		EmpConnector connector = new EmpConnector(params);

		try {
			connector.start().get(timeout, TimeUnit.SECONDS);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			throw new MessageHandlerException("failed to start", e);
		}

		TopicSubscription subscription;
		try {
			subscription = connector.subscribe(topic, replayFrom,
					consumer).get(5, TimeUnit.SECONDS);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			throw new MessageHandlerException("faild to subscribe",e);
		}
		return subscription;
	}

}
