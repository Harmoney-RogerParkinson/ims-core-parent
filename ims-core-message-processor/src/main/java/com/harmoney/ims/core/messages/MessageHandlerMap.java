package com.harmoney.ims.core.messages;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.harmoney.ims.core.partner.ConfiguredSalesforceParameters;
import com.salesforce.emp.connector.EmpConnector;

/**
 * Holds/Builds the map of Salesforce PushTopics, RabbitMQ queues and the relevant field resolver.
 * It also subscribes to the PushTopics.
 * 
 * @author Roger Parkinson
 *
 */
@Component
public class MessageHandlerMap {
	
	public static final String ILTIMS = "/topic/ILTIMS";
	public static final String IFTIMS = "/topic/IFTIMS";
	
    private static final Logger log = LoggerFactory.getLogger(MessageHandlerMap.class);

    @Autowired ConfiguredSalesforceParameters configuredParameters;

	@Autowired private EmpConnector empConnector;
	@Autowired private RabbitTemplate rabbitTemplate;
	
	private Map<String,MessageHandler> map = new HashMap<>();
		
	public MessageHandler getMessageHandler(String queueName) {
		return map.get(queueName);
	}
	@PostConstruct
	public void init() {
		subscribe(empConnector,ILTIMS,getMessageHandler("ilt-queue",new FieldResolverILT()));
		subscribe(empConnector,IFTIMS,getMessageHandler("ift-queue",new FieldResolverIFT()));

	}
	private MessageHandler getMessageHandler(String rabbitQueue, FieldResolver fieldResolver) {
		MessageHandler messageHandler = null;
		messageHandler = new MessageHandlerImpl(rabbitTemplate,rabbitQueue,fieldResolver);
		return messageHandler;
	}
	private void subscribe(EmpConnector connector, String thisTopic, MessageHandler thisMessageHandler) {
		try {
			Consumer<Map<String, Object>> consumer = event -> {
				thisMessageHandler.processMessage(event);
				};
			connector.subscribe(thisTopic, configuredParameters.getReplayFrom(),
					consumer).get(5, TimeUnit.SECONDS);
			log.debug("subscription created: {} replayFrom {}",thisTopic, configuredParameters.replayFrom);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			throw new MessageHandlerException("failed to subscribe",e);
		}
		map.put(thisTopic, thisMessageHandler);
		
	}

}
