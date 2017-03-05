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
@Profile("message-processor-prod")
public class MessageHandlerImpl implements MessageHandler {

	private static final Logger log = LoggerFactory
			.getLogger(MessageHandlerImpl.class);
	@Value("${rabbitmq.queue:transaction-queue}")
	public String queueName;
	@Autowired ConfigurableApplicationContext context;
	@Autowired RabbitTemplate rabbitTemplate;
	private String name;
	@Autowired FieldResolverFactory fieldResolverFactory;
	
	@Override
	public void processMessage(Map<String, Object> message) {
		log.debug("Received:\n{}", message);
		Map<String,Object> sobject = (Map<String,Object>)message.get("sobject");
		fieldResolverFactory.processFields(name,sobject);
		rabbitTemplate.convertAndSend(queueName, message);

	}

	@Override
	public void setTopicName(String name) {
		this.name = name;
		
	}


}
