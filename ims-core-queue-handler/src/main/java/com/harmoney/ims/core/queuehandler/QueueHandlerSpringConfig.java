package com.harmoney.ims.core.queuehandler;

import nz.co.senanque.madura.ampq.EnableAMPQ;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@EnableAMPQ
@ComponentScan(value={"com.harmoney.ims.core.queuehandler","com.harmoney.ims.core.queueprocessor"})
@PropertySource(value = { "classpath:test.properties" }, ignoreResourceNotFound = true)
public class QueueHandlerSpringConfig {

	 @Value("${rabbitmq.host:localhost}")
	 public String rabbitMQHost;
	 @Value("${rabbitmq.vhost:harmoney}")
	 public String rabbitMQvHost;
	 @Value("${rabbitmq.port:31761}")
	 public int rabbitmqPort;
	 @Value("${rabbitmq.username:harmoney}")
	 public String rabbitmqUsername;
	 @Value("${rabbitmq.password:harmoney}")
	 public String rabbitmqPassword;
	 @Value("${rabbitmq.exchange:transaction-exchange}")
	 public String exchangeName;
	 public static String ILTQUEUE = "ilt-queue"; 
	 public static String IFTQUEUE = "ift-queue"; 
	 
	 /**
	 * Although the rabbitTemplate is not used here the only other place the
	 * template is injected is a required=false (where false is okay for testing)
	 * We seem to need to have a required=true here to force it to inject there.
	 */
	private @Autowired RabbitTemplate rabbitTemplate;

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
	    CachingConnectionFactory factory = new CachingConnectionFactory(rabbitMQHost);
	    factory.setUsername(rabbitmqUsername);
	    factory.setPassword(rabbitmqPassword);
	    factory.setVirtualHost(rabbitMQvHost);

	    return factory;
	}
	// Only need this if we want to auto-create the queue and/or exchange
	@Bean
	RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
		return new RabbitAdmin(connectionFactory);
	}
	@Bean
	TopicExchange exchange() {
		return new TopicExchange(exchangeName);
	}

	@Bean
	Queue iltqueue() {
		return new Queue(ILTQUEUE, true);
	}

	@Bean
	Binding iltbinding(Queue iltqueue, TopicExchange exchange) {
		return BindingBuilder.bind(iltqueue).to(exchange).with(ILTQUEUE);
	}

	@Bean
	Queue iftqueue() {
		return new Queue(IFTQUEUE, true);
	}

	@Bean
	Binding iftbinding(Queue iftqueue, TopicExchange exchange) {
		return BindingBuilder.bind(iftqueue).to(exchange).with(IFTQUEUE);
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
	RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
		RabbitTemplate ret = new RabbitTemplate(connectionFactory);
		ret.setExchange(exchangeName);
		return ret;
	}

}
