/**
 * 
 */
package com.harmoney.ims.core.messages;

import java.util.Map;
import java.util.concurrent.CountDownLatch;

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
@Profile("message-processor-dev")
public class MessageHandlerMock implements com.harmoney.ims.core.messages.MessageHandler {
	
    private static final Logger log = LoggerFactory.getLogger(MessageHandlerMock.class);
    private CountDownLatch latch = new CountDownLatch(1);
    
	@Override
	public void processMessage(Map<String, Object> message) {
		log.debug("Received:\n{}", message);
		latch.countDown();
	}
    public CountDownLatch getLatch() {
        return latch;
    }

}
