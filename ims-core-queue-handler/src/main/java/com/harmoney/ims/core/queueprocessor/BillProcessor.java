package com.harmoney.ims.core.queueprocessor;


import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.harmoney.ims.core.database.BillDAO;
import com.harmoney.ims.core.database.ConvertUtils;
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
        log.info("Received <{}>", message);
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
    	Date eventDate = ConvertUtils.parseDate(message.get("event").get("createdDate").toString().substring(0,10));
        Bill sobject = DAO.unpackMessage(message.get("sobject"));
		Bill original = DAO.getByLoanAccountIdDueDate(sobject.getLoanAccountId(),sobject.getDueDate());
        if (original == null) {
        	// no previous version
        	DAO.create(sobject);
    		// Bill created: This will create the PRRs and fill with Management and Sales Commission.
        	amortizationScheduleProcessor.billPaymentUnsatisfied(sobject.getLoanAccountId(),sobject.isWaiverApplied(), addAMonthRoundingToEOM(sobject.getDueDate()));
        	if (sobject.isPaymentSatisfied()) {
        		// satisfied was set on create: go figure the protect realised values
            	amortizationScheduleProcessor.billPaymentSatisfied(sobject.getLoanAccountId(), sobject.isWaiverApplied(),sobject.getDueDate(),eventDate);
        	} else {
        		log.debug("No action to take (other than create)");
        	}
        } else {
        	boolean satisfiedFlagChanged = sobject.isPaymentSatisfied() != original.isPaymentSatisfied();
        	sobject.setImsid(original.getImsid());
        	DAO.copy(sobject, original);
        	if (satisfiedFlagChanged) {
        		// If the satisfaction flag changed then go process it.
            	if (sobject.isPaymentSatisfied()) {
                	amortizationScheduleProcessor.billPaymentSatisfied(sobject.getLoanAccountId(), sobject.isWaiverApplied(),sobject.getDueDate(),eventDate);
            	} else {
                	amortizationScheduleProcessor.billPaymentUnsatisfied(sobject.getLoanAccountId(), sobject.isWaiverApplied(),sobject.getDueDate());
            	}
        	} else {
        		log.debug("No action to take (other than update)");
        	}
        }
    }
	private Date addAMonthRoundingToEOM(Date in) {
        ZoneId defaultZoneId = ZoneId.systemDefault();
        Instant instant = Instant.ofEpochMilli(in.getTime());
		LocalDate d = instant.atZone(defaultZoneId).toLocalDate();
		d = d.plusMonths(1);
		instant = d.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
		return Date.from(instant);
	}


}
