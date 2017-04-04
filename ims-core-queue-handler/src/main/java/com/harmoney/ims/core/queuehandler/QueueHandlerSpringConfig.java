package com.harmoney.ims.core.queuehandler;

import nz.co.senanque.madura.ampq.EnableAMPQ;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@EnableAMPQ
@ComponentScan(value={"com.harmoney.ims.core.queuehandler","com.harmoney.ims.core.queueprocessor"})
@PropertySource(value = { "classpath:test.properties" }, ignoreResourceNotFound = true)
public class QueueHandlerSpringConfig {
	
	@Autowired ConfiguredQueueParameters configuredQueueParameters;

	 
	// needed for @PropertySource
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertyConfigInProd() {
		PropertySourcesPlaceholderConfigurer ret = new PropertySourcesPlaceholderConfigurer();
		return ret;
	}
	/**
	 * Bean is our direct connection to RabbitMQ
	 * @return CachingConnectionFactory
	 */
	@Bean(destroyMethod = "destroy")
	public ConnectionFactory rabbitConnectionFactory() {
	    CachingConnectionFactory factory = new CachingConnectionFactory(configuredQueueParameters.getRabbitMQHost());
	    factory.setUsername(configuredQueueParameters.getRabbitmqUsername());
	    factory.setPassword(configuredQueueParameters.getRabbitmqPassword());
	    factory.setVirtualHost(configuredQueueParameters.getRabbitMQvHost());

	    return factory;
	}
	// Only need this if we want to auto-create the queue and/or exchange
//	@Bean
//	RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
//		return new RabbitAdmin(connectionFactory);
//	}
	@Bean
	TopicExchange exchange() {
		return new TopicExchange(configuredQueueParameters.getExchangeName());
	}

	@Bean
	Queue iltqueue() {
		return new Queue(ConfiguredQueueParameters.ILTQUEUE, true);
	}

	@Bean
	Binding iltbinding(Queue iltqueue, TopicExchange exchange) {
		return BindingBuilder.bind(iltqueue).to(exchange).with(ConfiguredQueueParameters.ILTQUEUE);
	}

	@Bean
	Queue iftqueue() {
		return new Queue(ConfiguredQueueParameters.IFTQUEUE, true);
	}

	@Bean
	Binding iftbinding(Queue iftqueue, TopicExchange exchange) {
		return BindingBuilder.bind(iftqueue).to(exchange).with(ConfiguredQueueParameters.IFTQUEUE);
	}

	@Bean
	Queue billqueue() {
		return new Queue(ConfiguredQueueParameters.BILLQUEUE, true);
	}

	@Bean
	Binding billbinding(Queue iftqueue, TopicExchange exchange) {
		return BindingBuilder.bind(iftqueue).to(exchange).with(ConfiguredQueueParameters.BILLQUEUE);
	}

	@Bean
	Queue loanaccountqueue() {
		return new Queue(ConfiguredQueueParameters.LOANACCOUNT, true);
	}

	@Bean
	Binding loanaccountbinding(Queue iftqueue, TopicExchange exchange) {
		return BindingBuilder.bind(iftqueue).to(exchange).with(ConfiguredQueueParameters.LOANACCOUNT);
	}

//	@Bean
//	SimpleMessageListenerContainer container(
//			ConnectionFactory connectionFactory,
//			MessageListenerAdapter listenerAdapter) {
//		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
//		container.setConnectionFactory(connectionFactory);
//		container.setQueueNames(queueName);
//		container.setMessageListener(listenerAdapter);
//		return container;
//	}
//
//	@Bean
//	MessageListenerAdapter listenerAdapter(ReceiverMock receiver) {
//		return new MessageListenerAdapter(receiver, "receiveMessage");
//	}

	@Bean
	@Profile({"queue-handler-dev","queue-handler-prod"})
	RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
		RabbitTemplate ret = new RabbitTemplate(connectionFactory);
		ret.setExchange(configuredQueueParameters.getExchangeName());
		return ret;
	}

}
