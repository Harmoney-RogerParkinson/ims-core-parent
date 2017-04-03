package com.harmoney.ims.core.messages;

import static com.salesforce.emp.connector.LoginHelper.login;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplateMock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import com.harmoney.ims.core.partner.ConfiguredSalesforceParameters;
import com.salesforce.emp.connector.BayeuxParameters;
import com.salesforce.emp.connector.EmpConnector;

/**
 * This is a Spring Configuration class that defines the beans needed for the
 * database.
 * 
 * @author Roger Parkinson
 *
 */
@Configuration
@ComponentScan("com.harmoney.ims.core.messages")
public class MessageProcessorSpringConfig {
	
	@Autowired ConfiguredSalesforceParameters configuredParameters;
	
    private static final Logger log = LoggerFactory.getLogger(MessageProcessorSpringConfig.class);

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
			log.debug("starting login: {} {}",configuredParameters.getSalesforceURL(),configuredParameters.getUsername());
			params = login(new URL(configuredParameters.getSalesforceURL()), configuredParameters.getUsername(), configuredParameters.getPassword());
			log.info("login successful: {} {}",configuredParameters.getSalesforceURL(),configuredParameters.getUsername());
		} catch (Exception e) {
			throw new MessageHandlerException("failed to login",e);
		}
		EmpConnector connector = new EmpConnector(params);

		try {
			connector.start().get(configuredParameters.getTimeout(), TimeUnit.SECONDS);
			log.debug("connector started");
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			throw new MessageHandlerException("failed to start", e);
		}
		return connector;
	}
	@Bean
	@Profile("message-processor-dev")
	RabbitTemplate rabbitTemplate() {
		RabbitTemplate ret = new RabbitTemplateMock();
		return ret;
	}
	
	/**
	 * The queueing handlers are defined here, including the mapping between pushtopics and rabbit queues.
	 * There is also an optional field resolver class.
	 * @return
	 */
	@Bean
	List<MessageConfigurationEntry> getMessageConfiguration() {
		List<MessageConfigurationEntry> ret = new ArrayList<>();
//		ret.add(new MessageConfigurationEntry("/topic/ILTIMS","ilt-queue",new FieldResolverILT()));
//		ret.add(new MessageConfigurationEntry("/topic/IFTIMS","ift-queue"));
		ret.add(new MessageConfigurationEntry("/topic/BILL","bill-queue"));
		ret.add(new MessageConfigurationEntry("/topic/LOANACCOUNT","loanaccount-queue"));
		return ret;
	}
	

}
