package com.harmoney.ims.core.balanceforward;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.harmoney.ims.core.database.InvestorLoanTransactionDAO;

public class InvestorLoanTransactionBalanceForward {
	
    @Autowired private InvestorLoanTransactionDAO investorLoanTransactionDAO;
    
    /**
     * Generate or update balance forward records for every account id for the period given.
     * The date given can be any in the requested period.
     *  
     * @param date
     */
    public void processBalanceForward(LocalDate date) {
    	LocalDateTime lastMomentOfLastMonth = date.minusMonths(1).with(TemporalAdjusters.lastDayOfMonth()).atTime(23, 59, 59, 999999999);   	 
    	LocalDateTime lastMomentOfMonth = date.with(TemporalAdjusters.lastDayOfMonth()).atTime(23, 59, 59, 999999999);
    	
    	List<String> accountIds = investorLoanTransactionDAO.getAccountIds(lastMomentOfLastMonth,lastMomentOfMonth);
    	for (String accountId: accountIds) {
    		investorLoanTransactionDAO.processBalanceForward(lastMomentOfLastMonth, lastMomentOfMonth, accountId);
    	}
    }

    
}
