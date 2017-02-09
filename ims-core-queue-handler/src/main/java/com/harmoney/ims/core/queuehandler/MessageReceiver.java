/**
 * 
 */
package com.harmoney.ims.core.queuehandler;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.stereotype.Component;

/**
 * @author Roger Parkinson
 *
 */
@Component
public class MessageReceiver implements MessageListener {
 
    static final Logger LOG = LoggerFactory.getLogger(MessageReceiver.class);
     
    @Autowired
    MessageConverter messageConverter;
     
    @Override
    public void onMessage(Message message) {
        try {
            LOG.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++");
//            InventoryResponse response = (InventoryResponse) messageConverter.fromMessage(message);
            LOG.info("Application : order response received : {}",message);    
            LOG.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
         
    }
}

