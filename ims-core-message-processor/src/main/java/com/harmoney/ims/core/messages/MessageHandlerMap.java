package com.harmoney.ims.core.messages;

import java.util.HashMap;
import java.util.List;
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
	
    private static final Logger log = LoggerFactory.getLogger(MessageHandlerMap.class);

    @Autowired ConfiguredSalesforceParameters configuredParameters;
    @Autowired List<MessageConfigurationEntry> messageConfiguration;

	@Autowired private EmpConnector empConnector;
	@Autowired private RabbitTemplate rabbitTemplate;
	
	private Map<String,MessageHandler> map = new HashMap<>();
		
	public MessageHandler getMessageHandler(String queueName) {
		return map.get(queueName);
	}
	@PostConstruct
	public void init() {
		for (MessageConfigurationEntry messageConfigurationEntry: messageConfiguration) {
			subscribe(empConnector,messageConfigurationEntry.getPushTopic(),
					getMessageHandler(messageConfigurationEntry.getRabbitQueue(),messageConfigurationEntry.getFieldResolver()));
		}
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
			log.info("subscription created: {} replayFrom {} rabbit: {}",thisTopic, configuredParameters.replayFrom,thisMessageHandler.getRabbitQueue());
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			throw new MessageHandlerException("failed to subscribe",e);
		}
		map.put(thisTopic, thisMessageHandler);
		
	}

}
