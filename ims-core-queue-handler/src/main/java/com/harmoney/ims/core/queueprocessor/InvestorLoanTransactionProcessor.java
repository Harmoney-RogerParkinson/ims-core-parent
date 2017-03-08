/**
 * 
 */
package com.harmoney.ims.core.queueprocessor;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.harmoney.ims.core.database.InvestorLoanTransactionDAO;
import com.harmoney.ims.core.database.descriptors.Result;
import com.harmoney.ims.core.instances.InvestorLoanTransaction;

/**
 * The incoming message for InvestorLoanTransactionProcessor arrives here.
 * It is a separate class from the queue handler to make it easier to detach from the queue
 * during testing.
 *
 * @author Roger Parkinson
 *
 */
@Component
public class InvestorLoanTransactionProcessor {

    private static final Logger log = LoggerFactory.getLogger(InvestorLoanTransactionProcessor.class);
    @Autowired private InvestorLoanTransactionDAO investorLoanTransactionDAO;

    public void receiveMessage(Map<String, Map<String, Object>> message) {
        log.debug("Received <{}>", message);
        String eventType = (String)message.get("event").get("type");
        InvestorLoanTransaction target = new InvestorLoanTransaction();
        Result result = investorLoanTransactionDAO.unpackMessage(message, target);
        log.debug("{}",result);
        // Documentation is inconsistent on pushTopics
        // We may never see the deleted and undeleted types
        switch (eventType) {
        case "created":
            investorLoanTransactionDAO.create(target);
        	break;
        case "updated":
        	// locate the existing object and unpack the result into that.
        	investorLoanTransactionDAO.update(target);
        	break;
        case "deleted":
        	// generate a reversal object and persist it
            investorLoanTransactionDAO.delete(target);
        	break;
        case "undeleted":
        	// generate a new object and persist it
            investorLoanTransactionDAO.create(target);
        	break;
        }
    }
}
