package com.harmoney.ims.core.queueprocessor;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.harmoney.ims.core.database.InvestmentOrderDAO;
import com.harmoney.ims.core.database.descriptors.Result;
import com.sforce.soap.partner.sobject.SObject;

@Component
public class InvestmentOrderProcessor {
	
    private static final Logger log = LoggerFactory.getLogger(InvestmentOrderProcessor.class);
	
	@Autowired private InvestmentOrderDAO investmentOrderDAO;

	@Transactional
	public void processQuery(SObject[] records) {
		for (SObject sobject: records) {
			Result result = investmentOrderDAO.createOrUpdate(sobject);
			if (!result.isSuccess()) {
				log.debug("Result: {}",result);
			}
		}
	}
	

}
