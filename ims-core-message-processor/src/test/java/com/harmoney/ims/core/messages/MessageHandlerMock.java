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
@Profile("dev")
public class MessageHandlerMock implements MessageHandler {
	
    private static final Logger log = LoggerFactory.getLogger(MessageProcessorSpringConfig.class);
    private int messageCount = 0;
    private Thread ownerThread;
    
	public int getMessageCount() {
		return messageCount;
	}
	@Override
	public void processMessage(Map<String, Object> message) {
		log.debug("Received:\n{}", message);
		messageCount++;
		if (ownerThread != null) {
			ownerThread.interrupt();
		}
	}
	public Thread getOwnerThread() {
		return ownerThread;
	}
	public void setOwnerThread(Thread ownerThread) {
		this.ownerThread = ownerThread;
	}

}
