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

import com.harmoney.ims.core.queueprocessor.BillProcessor;
import com.harmoney.ims.core.queueprocessor.InvestorFundTransactionProcessor;
import com.harmoney.ims.core.queueprocessor.InvestorLoanTransactionProcessor;
import com.harmoney.ims.core.queueprocessor.LoanAccountProcessor;

/**
 * This is where the rabbit messages arrive and are subsequently dispatched to their
 * respective processors. The count() method is there to help with debugging and the
 * CountdownLatch is only used during testing, not production.
 * 
 * @author Roger Parkinson
 *
 */
@Component
@Profile("queue-handler-prod")
public class RabbitReceiver {

    private static final Logger log = LoggerFactory.getLogger(RabbitReceiver.class);
    
    @Autowired private InvestorLoanTransactionProcessor investorLoanTransactionProcessor;
    @Autowired private InvestorFundTransactionProcessor investorFundTransactionProcessor;
    @Autowired private BillProcessor billProcessor;
    @Autowired private LoanAccountProcessor loanAccountProcessor;

	private CountDownLatch latch;
	private long count = 0;

	@AMPQReceiver(queueName="ilt-queue")
    public void receiveILTMessage(Map<String, Map<String, Object>> message) {
        log.debug("Received <{}>", message);
        try {
			investorLoanTransactionProcessor.receiveMessage(message);
		} catch (Exception e) {
			log.error("receiveILTMessage",e);
		}
        count();
    }

    @AMPQReceiver(queueName="ift-queue")
    public void receiveIFTMessage(Map<String, Map<String, Object>> message) {
        log.debug("Received <{}>", message);
        try {
			investorFundTransactionProcessor.receiveMessage(message);
		} catch (Exception e) {
			log.error("receiveIFTMessage",e);
		}
        count();
    }

    @AMPQReceiver(queueName="bill-queue")
    public void receiveBillMessage(Map<String, Map<String, Object>> message) {
        log.debug("Received <{}>", message);
        try {
			billProcessor.receiveMessage(message);
		} catch (Exception e) {
			log.error("receiveBillMessage",e);
		}
        count();
    }

    @AMPQReceiver(queueName="loanaccount-queue")
    public void receiveLoanAccountMessage(Map<String, Map<String, Object>> message) {
        log.debug("Received <{}>", message);
        try {
			loanAccountProcessor.receiveMessage(message);
		} catch (Exception e) {
			log.error("receiveLoanAccountMessage",e);
		}
        count();
    }

	/**
	 * If a latch is set then count down any items that were
	 * processed before now. This ensures the latch is accurate.
	 * @param latch
	 */
	public void setLatch(CountDownLatch latch) {
		this.latch = latch;
		for (int i = 0; i<count; i++) {
			latch.countDown();
		}
	}
	/**
	 * This method counts down the latch is one is set (which it is only in testing)
	 * It also supports a lock on this object, allowing the test to block processing of incoming
	 * messages until it is ready to monitor them.
	 */
	private void count() {
		if (latch != null) {
			synchronized(this) {
				latch.countDown();
				count++;
			}
		}
	}

	public long getCount() {
		return count;
	}

}