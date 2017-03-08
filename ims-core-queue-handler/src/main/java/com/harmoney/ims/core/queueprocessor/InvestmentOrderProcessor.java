package com.harmoney.ims.core.queueprocessor;


import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.harmoney.ims.core.database.InvestmentOrderDAO;
import com.harmoney.ims.core.database.descriptors.Result;
import com.harmoney.ims.core.instances.InvestmentOrder;
import com.sforce.soap.partner.sobject.SObject;

@Component
public class InvestmentOrderProcessor {
	
    private static final Logger log = LoggerFactory.getLogger(InvestmentOrderProcessor.class);
	
	@Autowired private InvestmentOrderDAO investmentOrderDAO;

	@Transactional
	public void processQuery(SObject[] records) {
		Date createdDate = new Date();
		for (SObject sobject: records) {
			InvestmentOrder investmentOrder = new InvestmentOrder();
			Result result = investmentOrderDAO.unpack(sobject, investmentOrder);
			log.debug("Result: {}",result);
			investmentOrder.setCreatedDate(createdDate);
			investmentOrderDAO.create(investmentOrder);
		}
	}
	

}
