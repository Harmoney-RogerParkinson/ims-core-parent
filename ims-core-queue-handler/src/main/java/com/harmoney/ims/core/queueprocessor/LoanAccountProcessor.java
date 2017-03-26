package com.harmoney.ims.core.queueprocessor;


import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.harmoney.ims.core.database.LoanAccountDAO;
import com.harmoney.ims.core.database.descriptors.Result;
import com.harmoney.ims.core.instances.LoanAccount;
import com.harmoney.ims.core.instances.LoanAccountStatus;
import com.sforce.soap.partner.sobject.SObject;

@Component
public class LoanAccountProcessor {
	
    private static final Logger log = LoggerFactory.getLogger(LoanAccountProcessor.class);
	
	@Autowired private LoanAccountDAO DAO;
	@Autowired private AmortizationScheduleProcessor amortizationScheduleProcessor;

	public void processQuery(SObject[] records) {
		for (SObject sobject: records) {
			Result result = DAO.createOrUpdate(sobject);
			log.debug("Result: {}",result);
		}
	}

    public void receiveMessage(Map<String, Map<String, Object>> message) {
        log.debug("Received <{}>", message);
        String eventType = (String)message.get("event").get("type");
        switch (eventType) {
        case "created":
        case "updated":
        	processCreateOrUpdate(message);
        	break;
        default:
        	// Deleted and undeleted
        	log.warn("Unexpected event status: {}",eventType);
        }
    }
    
    private void processCreateOrUpdate(Map<String, Map<String, Object>> message) {
    	LoanAccount target = DAO.unpackMessage(message.get("sobject"));
    	String createdDate = message.get("event").get("createdDate").toString();
    	LoanAccount original = DAO.getById(target.getId());
        boolean statusClosed = false;
        boolean statusWaived = false;
        boolean statusActive = false;
        if (original == null) {
        	// Never seen this before, no previous status
        	if ((target.getStatus() == LoanAccountStatus.CLOSED_OBLIGATIONS_MET || target.getStatus() == LoanAccountStatus.CLOSED_WRITTEN_OFF)) {
        		statusClosed = true;
        	}
        	if ((target.getStatus() == LoanAccountStatus.ACTIVE_GOOD_STANDING)) {
        		statusActive = true;
        	}
        	DAO.create(target);
        } else {
        	if ((target.getStatus() == LoanAccountStatus.CLOSED_OBLIGATIONS_MET || target.getStatus() == LoanAccountStatus.CLOSED_WRITTEN_OFF) &&
        			(original.getStatus() == LoanAccountStatus.ACTIVE_GOOD_STANDING || original.getStatus() == LoanAccountStatus.ACTIVE_BAD_STANDING)) {
        		statusClosed = true;
        	}
        	if ((target.getStatus() == LoanAccountStatus.ACTIVE_GOOD_STANDING) &&
        			(original.getStatus() == LoanAccountStatus.APPROVED)) {
        		statusActive = true;
        	}
        	target.setImsid(original.getImsid());
        	DAO.merge(target);
        }
        if (statusClosed) {
        	
        	if (target.getStatus() == LoanAccountStatus.CLOSED_OBLIGATIONS_MET && target.isWaived()) {
        		statusWaived = true;
        	}
        	amortizationScheduleProcessor.loanAccountStatusClosed(target.getId(),statusWaived,createdDate);
        	return;
        }
        if (statusActive) {
        	
        	if (target.getStatus() == LoanAccountStatus.CLOSED_OBLIGATIONS_MET && target.isWaived()) {
        		statusWaived = true;
        	}
        	amortizationScheduleProcessor.loanAccountStatusActive(target.getId());
        	return;
        }
    	
    }

}
