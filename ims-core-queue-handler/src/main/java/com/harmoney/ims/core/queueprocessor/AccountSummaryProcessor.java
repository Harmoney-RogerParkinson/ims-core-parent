package com.harmoney.ims.core.queueprocessor;


import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
		LocalDateTime createdDate = LocalDateTime.now();
		for (SObject sobject: records) {
			AccountSummary accountSummary = new AccountSummary();
			Result result = accountSummaryDAO.unpack(sobject, accountSummary);
			log.debug("Result: {}",result);
			accountSummary.setCreatedDate(Timestamp.valueOf(createdDate));
			accountSummaryDAO.create(accountSummary);
		}
	}
	

}
