package com.harmoney.ims.core.balanceforward;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.harmoney.ims.core.database.AbstractTransactionDAO;
import com.harmoney.ims.core.database.InvestorLoanTransactionDAO;

@Component
public class InvestorLoanTransactionBalanceForward extends AbstractBalanceForward {
	  
    @Autowired private InvestorLoanTransactionDAO investorLoanTransactionDAO;
	protected AbstractTransactionDAO<?> getDAO() {
		return investorLoanTransactionDAO;
	}
    
    
}
