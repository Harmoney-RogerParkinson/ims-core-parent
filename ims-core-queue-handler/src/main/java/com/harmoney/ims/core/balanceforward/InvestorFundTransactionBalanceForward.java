package com.harmoney.ims.core.balanceforward;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.harmoney.ims.core.database.AbstractDAO;
import com.harmoney.ims.core.database.InvestorFundTransactionDAO;

@Component
public class InvestorFundTransactionBalanceForward extends AbstractBalanceForward {
	
    @Autowired private InvestorFundTransactionDAO investorFundTransactionDAO;
    
	protected AbstractDAO<?> getDAO() {
		return investorFundTransactionDAO;
	}
    
}
