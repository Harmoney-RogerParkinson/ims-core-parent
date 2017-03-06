/**
 * 
 */
package com.harmoney.ims.core.messages;

import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * Messages are delivered here for processing. Then they are forward to the rabbitMQ queue
 * unless the template is null, in which case it is just a test and we ignore Rabbit.
 * 
 * @author Roger Parkinson
 *
 */
public class MessageHandlerImpl implements MessageHandler {

	private static final Logger log = LoggerFactory
			.getLogger(MessageHandlerImpl.class);
	public String rabbbitQueue;
	private final RabbitTemplate rabbitTemplate;
	private final FieldResolver fieldResolver;
	
	public MessageHandlerImpl(RabbitTemplate rabbitTemplate, String rabbitQueue,
			FieldResolver fieldResolver) {
		this.rabbbitQueue = rabbitQueue;
		this.fieldResolver = fieldResolver;
		this.rabbitTemplate = rabbitTemplate;
	}

	@Override
	public void processMessage(Map<String, Object> message) {
		log.debug("Received:\n{}", message);
		Map<String,Object> sobject = (Map<String,Object>)message.get("sobject");
		fieldResolver.resolve(sobject);
		if (rabbitTemplate != null) {
			rabbitTemplate.convertAndSend(rabbbitQueue, message);
		}
	}
}
