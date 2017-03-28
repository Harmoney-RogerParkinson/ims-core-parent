package com.harmoney.ims.core.queueprocessor;


import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    private void processCreateOrUpdate(Map<String, Map<String, Object>> message) {
    	LoanAccount sobject = DAO.unpackMessage(message.get("sobject"));
    	String eventDate = message.get("event").get("createdDate").toString();
    	LoanAccount original = DAO.getById(sobject.getId());
        boolean statusClosed = false;
        boolean statusWaived = false;
        boolean statusActive = false;
        if (original == null) {
        	// Never seen this before, no previous status
        	if ((sobject.getStatus() == LoanAccountStatus.CLOSED_OBLIGATIONS_MET || sobject.getStatus() == LoanAccountStatus.CLOSED_WRITTEN_OFF)) {
        		statusClosed = true;
        	}
        	if ((sobject.getStatus() == LoanAccountStatus.ACTIVE_GOOD_STANDING)) {
        		statusActive = true;
        	}
        	DAO.create(sobject);
        } else {
        	if ((sobject.getStatus() == LoanAccountStatus.CLOSED_OBLIGATIONS_MET || sobject.getStatus() == LoanAccountStatus.CLOSED_WRITTEN_OFF) &&
        			(original.getStatus() == LoanAccountStatus.ACTIVE_GOOD_STANDING || original.getStatus() == LoanAccountStatus.ACTIVE_BAD_STANDING)) {
        		statusClosed = true;
        	}
        	if ((sobject.getStatus() == LoanAccountStatus.ACTIVE_GOOD_STANDING) &&
        			(original.getStatus() == LoanAccountStatus.APPROVED)) {
        		statusActive = true;
        	}
        	sobject.setImsid(original.getImsid());
        	DAO.copy(sobject,original);
        }
        if (statusClosed) {
        	
        	if (sobject.getStatus() == LoanAccountStatus.CLOSED_OBLIGATIONS_MET && sobject.isWaived()) {
        		statusWaived = true;
        	}
        	amortizationScheduleProcessor.loanAccountStatusClosed(sobject.getId(),statusWaived,eventDate);
        	return;
        }
        if (statusActive) {
        	
        	amortizationScheduleProcessor.loanAccountStatusActive(sobject.getId());
        	return;
        }
    	
    }

}
