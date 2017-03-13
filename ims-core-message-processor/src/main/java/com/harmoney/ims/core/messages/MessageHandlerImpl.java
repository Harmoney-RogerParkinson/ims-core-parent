/**
 * 
 */
package com.harmoney.ims.core.messages;

import java.util.Map;
import java.util.Map.Entry;
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
	public String rabbitQueue;
	private final RabbitTemplate rabbitTemplate;
	private final FieldResolver fieldResolver;
	private CountDownLatch latch;
	private long count = 0;
	
	public MessageHandlerImpl(RabbitTemplate rabbitTemplate, String rabbitQueue,
			FieldResolver fieldResolver) {
		this.rabbitQueue = rabbitQueue;
		this.fieldResolver = fieldResolver;
		this.rabbitTemplate = rabbitTemplate;
	}

	@Override
	public void processMessage(Map<String, Object> message) {
		log.debug("Received:\n{}", message);
		Map<String,Object> sobject = (Map<String,Object>)message.get("sobject");
		fieldResolver.resolve(sobject);
		if (log.isDebugEnabled()) {
			log.debug("sobject:\n {}",getSobject(sobject));
		}

		if (latch != null) {
			latch.countDown();
		}
		if (rabbitTemplate != null) {
			rabbitTemplate.convertAndSend(rabbitQueue, message);
			log.debug("Sent to: {}", rabbitQueue);
		}
		count++;
	}

	public void setLatch(CountDownLatch latch) {
		this.latch = latch;
		for (int i = 0; i<count; i++) {
			latch.countDown();
		}
	}
	
    private String getSobject(Map<String,Object> sobject) {
    	StringBuilder ret = new StringBuilder();
		for (Entry<String,Object> entry: sobject.entrySet()) {
			ret.append(entry.getKey());
			ret.append('=');
			ret.append(entry.getValue());
			ret.append('\n');
		}
		return ret.toString();
    }

	public long getCount() {
		return count;
	}

}
