package org.springframework.amqp.rabbit.core;

import org.springframework.amqp.AmqpException;

/**
 * Provides a rabbit template that actually does nothing. Used in testing.
 * 
 * @author Roger Parkinson
 *
 */
public class RabbitTemplateMock extends RabbitTemplate {
	
	@Override
	public void afterPropertiesSet() {
		//Assert.notNull(this.connectionFactory, "ConnectionFactory is required");
	}
	

	@Override
	public void convertAndSend(String routingKey, final Object object) throws AmqpException {
		// Do nothing (this is a mock)
	}
}
