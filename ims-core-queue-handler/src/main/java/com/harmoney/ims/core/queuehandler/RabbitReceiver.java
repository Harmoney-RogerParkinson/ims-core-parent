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

import com.harmoney.ims.core.queueprocessor.InvestorFundTransactionProcessor;
import com.harmoney.ims.core.queueprocessor.InvestorLoanTransactionProcessor;

/**
 * @author Roger Parkinson
 *
 */
@Component
@Profile("queue-handler-prod")
public class RabbitReceiver {

    private static final Logger log = LoggerFactory.getLogger(RabbitReceiver.class);
    
    @Autowired private InvestorLoanTransactionProcessor investorLoanTransactionProcessor;
    @Autowired private InvestorFundTransactionProcessor investorFundTransactionProcessor;

    @AMPQReceiver(queueName="ilt-queue")
    public void receiveILTMessage(Map<String, Map<String, Object>> message) {
        log.debug("Received <{}>", message);
        investorLoanTransactionProcessor.receiveMessage(message);
    }

    @AMPQReceiver(queueName="ift-queue")
    public void receiveIFTMessage(Map<String, Map<String, Object>> message) {
        log.debug("Received <{}>", message);
        investorFundTransactionProcessor.receiveMessage(message);
    }

}