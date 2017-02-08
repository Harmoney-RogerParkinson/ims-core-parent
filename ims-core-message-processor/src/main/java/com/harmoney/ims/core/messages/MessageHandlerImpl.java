/**
 * 
 */
package com.harmoney.ims.core.messages;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Messages are delivered here for processing.
 * 
 * @author Roger Parkinson
 *
 */
@Component
@Profile("prod")
public class MessageHandlerImpl implements MessageHandler {
	
    private static final Logger log = LoggerFactory.getLogger(MessageProcessorSpringConfig.class);

	@Override
	public void processMessage(Map<String,Object> message) {
		log.debug("Received:\n{}", message);
		
	}

}
