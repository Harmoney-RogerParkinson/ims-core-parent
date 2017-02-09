/**
 * 
 */
package com.harmoney.ims.core.queuehandler;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/**
 * @author Roger Parkinson
 *
 */
@Component
public class SimpleReceiver {


	public SimpleReceiver() {
		// TODO Auto-generated constructor stub
	}
	
	 @JmsListener(destination="transaction-queue")
	 public void process(String msg) {
	     // process incoming message
	 }

}
