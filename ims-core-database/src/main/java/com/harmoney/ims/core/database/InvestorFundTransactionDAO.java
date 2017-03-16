package com.harmoney.ims.core.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.harmoney.ims.core.instances.InvestorFundTransaction;

@Repository
public class InvestorFundTransactionDAO extends AbstractTransactionDAO<InvestorFundTransaction> {

	private static final Logger log = LoggerFactory.getLogger(InvestorFundTransactionDAO.class);

}
