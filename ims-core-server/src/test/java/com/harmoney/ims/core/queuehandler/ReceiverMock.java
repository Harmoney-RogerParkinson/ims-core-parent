/**
 * 
 */
package com.harmoney.ims.core.queuehandler;

import java.util.Map;
import java.util.concurrent.CountDownLatch;

import nz.co.senanque.madura.ampq.AMPQReceiver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.harmoney.ims.core.queueprocessor.InvestorLoanTransactionProcessor;

/**
 * @author Roger Parkinson
 *
 */
@Component
@Profile("server-dev")
public class ReceiverMock {

    private static final Logger log = LoggerFactory.getLogger(ReceiverMock.class);
    
    private CountDownLatch latch = new CountDownLatch(7);
    
    @Autowired private InvestorLoanTransactionProcessor investorLoanTransactionProcessor;
    
    @AMPQReceiver(queueName="ilt-queue")
    public void receiveILTMessage(Map<String, Map<String, Object>> message) {
        log.debug("Received <{}>", message);
        investorLoanTransactionProcessor.receiveMessage(message);
        latch.countDown();
    }
    @AMPQReceiver(queueName="ift-queue")
    public void receiveIFTMessage(Map<String, Map<String, Object>> message) {
        log.debug("Received <{}>", message);
//        investorLoanTransactionProcessor.receiveMessage(message);
        latch.countDown();
    }
    public CountDownLatch getLatch() {
        return latch;
    }

}