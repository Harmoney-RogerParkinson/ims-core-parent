package com.harmoney.ims.core.queueprocessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.harmoney.ims.core.database.AmortizationScheduleDAO;
import com.harmoney.ims.core.database.descriptors.Result;
import com.sforce.soap.partner.sobject.SObject;

@Component
public class AmortizationScheduleProcessor {

    private static final Logger log = LoggerFactory.getLogger(InvestmentOrderProcessor.class);
	
	@Autowired private AmortizationScheduleDAO amortizationScheduleDAO;

	@Transactional
	public void processQuery(SObject[] records) {
		for (SObject sobject: records) {
			Result result = amortizationScheduleDAO.createOrUpdate(sobject);
			log.debug("Result: {}",result);
		}
	}
}
