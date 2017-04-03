package com.harmoney.ims.core.queueprocessor;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.harmoney.ims.core.database.AccountDAO;
import com.harmoney.ims.core.database.descriptors.Result;
import com.sforce.soap.partner.sobject.SObject;

@Component
public class AccountProcessor {
	
    private static final Logger log = LoggerFactory.getLogger(AccountProcessor.class);
	
	@Autowired private AccountDAO accountDAO;

	public void processQuery(SObject[] records) {
		for (SObject sobject: records) {
			Result result = accountDAO.createOrUpdate(sobject);
			if (!result.isSuccess()) {
				log.debug("Result: {}",result);
			}
		}
	}
	

}
