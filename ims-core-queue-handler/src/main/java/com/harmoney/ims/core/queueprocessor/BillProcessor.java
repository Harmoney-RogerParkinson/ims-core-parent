package com.harmoney.ims.core.queueprocessor;


import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.harmoney.ims.core.database.BillDAO;
import com.harmoney.ims.core.database.descriptors.Result;
import com.harmoney.ims.core.instances.Bill;
import com.sforce.soap.partner.sobject.SObject;

@Component
public class BillProcessor {
	
    private static final Logger log = LoggerFactory.getLogger(BillProcessor.class);
	
	@Autowired private BillDAO DAO;
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
        Bill target = DAO.unpackMessage(message.get("sobject"));
        Bill original = DAO.getById(target.getId());
        if (original == null) {
        	// no previous version
        	DAO.create(target);
        	amortizationScheduleProcessor.billCreated(target.getLoanAccountId(), target.getDueDate());
        	if (target.isPaymentSatisfied()) {
        		// satisfied was set on create (unlikely unless we are back filling)
            	amortizationScheduleProcessor.billPaymentSatisfied(target.getLoanAccountId(), target.isWaiverApplied(),target.getDueDate());
        	}
        } else {
        	target.setImsid(original.getImsid());
        	DAO.merge(target);
        	if (target.isPaymentSatisfied() != original.isPaymentSatisfied()) {
            	if (target.isPaymentSatisfied()) {
            		// satisfied was set on create (unlikely unless we are back filling)
                	amortizationScheduleProcessor.billPaymentSatisfied(target.getLoanAccountId(), target.isWaiverApplied(),target.getDueDate());
            	} else {
                	amortizationScheduleProcessor.billPaymentUnsatisfied(target.getLoanAccountId(), target.isWaiverApplied(),target.getDueDate());
            	}
        	}
        }
    }

}
