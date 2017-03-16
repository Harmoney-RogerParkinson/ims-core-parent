package com.harmoney.ims.core.queueprocessor;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.harmoney.ims.core.database.AccountDAO;
import com.harmoney.ims.core.database.descriptors.Result;
import com.harmoney.ims.core.instances.Account;
import com.sforce.soap.partner.sobject.SObject;

@Component
public class AccountProcessor {
	
    private static final Logger log = LoggerFactory.getLogger(AccountProcessor.class);
	
	@Autowired private AccountDAO accountSummaryDAO;
	@Autowired private UnpackHelper unpackHelper;

	@Transactional
	public void processQuery(SObject[] records) {
		for (SObject sobject: records) {
			Account accountSummary = new Account();
			Result result = unpackHelper.unpack(sobject, accountSummary,accountSummaryDAO.getObjectDescriptor());
			log.debug("Result: {}",result);
			accountSummaryDAO.create(accountSummary);
		}
	}
	

}
