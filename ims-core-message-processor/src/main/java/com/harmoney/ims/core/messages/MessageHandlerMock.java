/**
 * 
 */
package com.harmoney.ims.core.messages;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Messages are delivered here for processing.
 * 
 * @author Roger Parkinson
 *
 */
public class MessageHandlerMock implements com.harmoney.ims.core.messages.MessageHandler {
	
    private static final Logger log = LoggerFactory.getLogger(MessageHandlerMock.class);
    private CountDownLatch latch = new CountDownLatch(1);
	private String name;
	private FieldResolver fieldResolver;
    
	public MessageHandlerMock(FieldResolver fieldResolver) {
		this.fieldResolver = fieldResolver;
	}
	@Override
	public void processMessage(Map<String, Object> message) {
		log.debug("Received:\n{}", message);
		Map<String,Object> sobject = (Map<String,Object>)message.get("sobject");
		fieldResolver.resolve(sobject);
		log.debug("sobject:\n {}",getSobject(sobject));

		latch.countDown();
	}
    public CountDownLatch getLatch() {
        return latch;
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
	@Override
	public void setLatch(CountDownLatch latch) {
		// TODO Auto-generated method stub
		
	}

}
