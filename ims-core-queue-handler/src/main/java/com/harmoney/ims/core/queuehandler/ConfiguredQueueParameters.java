/**
 * 
 */
package com.harmoney.ims.core.queuehandler;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Roger Parkinson
 *
 */
@Component
public class ConfiguredQueueParameters {
	
	private static final Logger log = LoggerFactory.getLogger(ConfiguredQueueParameters.class);

	 @Value("${rabbitmq.host:localhost}")
	 private String rabbitMQHost;
	 @Value("${rabbitmq.vhost:harmoney}")
	 private String rabbitMQvHost;
	 @Value("${rabbitmq.port:31761}")
	 private int rabbitmqPort;
	 @Value("${rabbitmq.username:harmoney}")
	 private String rabbitmqUsername;
	 @Value("${rabbitmq.password:harmoney}")
	 private String rabbitmqPassword;
	 @Value("${rabbitmq.exchange:transaction-exchange}")
	 private String exchangeName;
	 public static String ILTQUEUE = "ilt-queue"; 
	 public static String IFTQUEUE = "ift-queue"; 

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("\nrabbitMQHost: ");
		sb.append(rabbitMQHost);
		sb.append("\nrabbitMQvHost: ");
		sb.append(rabbitMQvHost);
		sb.append("\nrabbitmqPort: ");
		sb.append(rabbitmqPort);
		sb.append("\nrabbitmqUsername: ");
		sb.append(rabbitmqUsername);
		sb.append("\nexchangeName: ");
		sb.append(exchangeName);
		return sb.toString();
	}
	@PostConstruct
	public void init() {
		log.info("Queue configuration: {}",this);
	}
	public String getRabbitMQHost() {
		return rabbitMQHost;
	}
	public String getRabbitMQvHost() {
		return rabbitMQvHost;
	}
	public int getRabbitmqPort() {
		return rabbitmqPort;
	}
	public String getRabbitmqUsername() {
		return rabbitmqUsername;
	}
	public String getRabbitmqPassword() {
		return rabbitmqPassword;
	}
	public String getExchangeName() {
		return exchangeName;
	}
}
