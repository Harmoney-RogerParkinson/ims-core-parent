/**
 * 
 */
package com.harmoney.ims.core.queuehandler;

import java.util.Map;

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
@Profile("queue-handler-prod")
public class InvestorLoanTransactionReceiver {

    private static final Logger log = LoggerFactory.getLogger(InvestorLoanTransactionReceiver.class);
    
    @Autowired private InvestorLoanTransactionProcessor investorLoanTransactionProcessor;

    @AMPQReceiver(queueName="${rabbitmq.queue:transaction-queue}")
    public void receiveMessage(Map<String, Map<String, Object>> message) {
        log.debug("Received <{}>", message);
        investorLoanTransactionProcessor.receiveMessage(message);
    }

}