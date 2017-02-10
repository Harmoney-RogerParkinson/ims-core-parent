/**
 * 
 */
package com.harmoney.ims.core.messages;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Messages are delivered here for processing. Then they are forward to the rabbitMQ queue
 * 
 * @author Roger Parkinson
 *
 */
@Component
@Profile("prod")
public class MessageHandlerImpl implements MessageHandler {

	private static final Logger log = LoggerFactory
			.getLogger(MessageHandlerImpl.class);
	@Value("${rabbitmq.queue:transaction-queue}")
	public String queueName;
	@Autowired ConfigurableApplicationContext context;
	@Autowired RabbitTemplate rabbitTemplate;

	@Override
	public void processMessage(Map<String, Object> message) {
		log.debug("Received:\n{}", message);
		// TODO: Some message deblocking needs to happen here if incoming messages
		// have multiple records in a single message.
		rabbitTemplate.convertAndSend(queueName, message);

	}

}
