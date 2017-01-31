/**
 * 
 */
package com.harmoney.ims.core.messages;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.harmoney.ims.core.messages.MessageHandler;
import com.harmoney.ims.core.messages.SpringConfig;

/**
 * Messages are delivered here for processing.
 * 
 * @author Roger Parkinson
 *
 */
@Component
@Profile("dev")
public class MessageHandlerMock implements MessageHandler {
	
    private static final Logger log = LoggerFactory.getLogger(SpringConfig.class);
	public void processMessage(Map<String,Object> message) {
		log.debug("Received:\n{}", message);
		
	}

}
