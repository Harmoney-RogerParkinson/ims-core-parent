package com.harmoney.ims.core.balanceforward;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harmoney.ims.core.database.AbstractTransactionDAO;

abstract class AbstractBalanceForward {

    private static final Logger log = LoggerFactory.getLogger(AbstractBalanceForward.class);

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

    	LocalDateTime lastMomentOfLastMonth = getDAO().getLastMomentOfLastMonth(date);
    	LocalDateTime lastMomentOfMonth = getDAO().getLastMomentOfMonth(date);
    	
    	List<String> accountIds = getDAO().getAccountIds(lastMomentOfLastMonth,lastMomentOfMonth);
    	BalanceForwardDTO ret = new BalanceForwardDTO(lastMomentOfLastMonth,lastMomentOfMonth,accountIds);

    	log.debug("found {} accounts to process",accountIds.size());
    	for (String accountId: accountIds) {
    		log.debug("Starting account {}",accountId);
    		int i = getDAO().processBalanceForward(lastMomentOfLastMonth, lastMomentOfMonth, accountId);
    		if (testMode) {
    			ret.put(accountId,i);
    		}
    		log.debug("Finished account {}",accountId);
    	}
    	log.debug("Account processing complete\n");
    	return ret;
    	
    }

    abstract AbstractTransactionDAO<?> getDAO();

	public void setTestMode(boolean testMode) {
		this.testMode = testMode;
	}
	
}
