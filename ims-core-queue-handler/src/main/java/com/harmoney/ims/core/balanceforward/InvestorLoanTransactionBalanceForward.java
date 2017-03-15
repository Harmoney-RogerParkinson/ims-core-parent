package com.harmoney.ims.core.balanceforward;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.harmoney.ims.core.database.InvestorLoanTransactionDAO;

@Component
public class InvestorLoanTransactionBalanceForward {
	
    private static final Logger log = LoggerFactory.getLogger(InvestorLoanTransactionBalanceForward.class);
    
    /**
     * A few nanoseconds before midnight, should be the last thing that happens that day.
     * The database doesn't store precision more than 999999000
     */
    private static final LocalTime LAST_MOMENT = LocalTime.of(23, 59, 59, 999999000);

    @Autowired private InvestorLoanTransactionDAO investorLoanTransactionDAO;
	@Value("${InvestorLoanTransactionBalanceForward.testMode:false}")
	public boolean testMode;

    
    /**
     * Generate or update balance forward records for every account id for the period given.
     * The date given can be any in the requested period.
     * The returned object is used for testing.
     *  
     * @param date
     * @return BalanceForwardDTO
     */
    public BalanceForwardDTO processBalanceForward(LocalDate date) {
    	
    	LocalDateTime lastMomentOfLastMonth = date.minusMonths(1).with(TemporalAdjusters.lastDayOfMonth()).atTime(LAST_MOMENT);   	 
    	LocalDateTime lastMomentOfMonth = date.with(TemporalAdjusters.lastDayOfMonth()).atTime(LAST_MOMENT);
   	
    	List<String> accountIds = investorLoanTransactionDAO.getAccountIds(lastMomentOfLastMonth,lastMomentOfMonth);
    	BalanceForwardDTO ret = new BalanceForwardDTO(lastMomentOfLastMonth,lastMomentOfMonth,accountIds);

    	log.debug("found {} accounts to process",accountIds.size());
    	for (String accountId: accountIds) {
    		log.debug("Starting account {}",accountId);
    		int i = investorLoanTransactionDAO.processBalanceForward(lastMomentOfLastMonth, lastMomentOfMonth, accountId);
    		if (testMode) {
    			ret.put(accountId,i);
    		}
    		log.debug("Finished account {}",accountId);
    	}
    	log.debug("Account processing complete\n");
    	return ret;
    	
    }

    
}
