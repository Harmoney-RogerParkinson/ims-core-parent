package com.harmoney.ims.core.queueprocessor;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.harmoney.ims.core.database.AccountSummaryDAO;
import com.harmoney.ims.core.database.descriptors.Result;
import com.harmoney.ims.core.instances.AccountSummary;
import com.sforce.soap.partner.sobject.SObject;

@Component
public class AccountSummaryProcessor {
	
    private static final Logger log = LoggerFactory.getLogger(AccountSummaryProcessor.class);
	
	@Autowired private AccountSummaryDAO accountSummaryDAO;

	@Transactional
	public void processQuery(SObject[] records) {
		Date createdDate = new Date();
		for (SObject sobject: records) {
			AccountSummary accountSummary = new AccountSummary();
			Result result = accountSummaryDAO.unpack(sobject, accountSummary);
			log.debug("Result: {}",result);
			accountSummary.setCreatedDate(createdDate);
			accountSummaryDAO.create(accountSummary);
		}
	}
	

}
