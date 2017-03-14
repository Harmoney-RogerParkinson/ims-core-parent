package com.harmoney.ims.core.balanceforward;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.harmoney.ims.core.database.InvestorLoanTransactionDAO;

@Component
public class InvestorLoanTransactionBalanceForward {
	
    private static final Logger log = LoggerFactory.getLogger(InvestorLoanTransactionBalanceForward.class);

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
    		log.debug("Starting account {}",accountId);
    		investorLoanTransactionDAO.processBalanceForward(lastMomentOfLastMonth, lastMomentOfMonth, accountId);
    		log.debug("Finished account {}",accountId);
    	}
    }

    
}
