package com.harmoney.ims.core.queueprocessor;


import java.sql.Timestamp;
import java.time.LocalDateTime;

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
	@Autowired private UnpackHelper unpackHelper;

	@Transactional
	public void processQuery(SObject[] records) {
		LocalDateTime createdDate = LocalDateTime.now();
		for (SObject sobject: records) {
			InvestmentOrder investmentOrder = new InvestmentOrder();
			Result result = unpackHelper.unpack(sobject, investmentOrder,investmentOrderDAO.getObjectDescriptor());
			log.debug("Result: {}",result);
			investmentOrder.setCreatedDate(Timestamp.valueOf(createdDate));
			investmentOrderDAO.create(investmentOrder);
		}
	}
	

}
