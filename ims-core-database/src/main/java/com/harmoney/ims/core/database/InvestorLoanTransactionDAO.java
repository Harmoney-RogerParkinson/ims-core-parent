/**
 * 
 */
package com.harmoney.ims.core.database;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.harmoney.ims.core.database.descriptors.ObjectDescriptor;
import com.harmoney.ims.core.instances.InvestorLoanTransaction;
import com.harmoney.ims.core.instances.ItemType;

/**
 * @author Roger Parkinson
 *
 */
@Repository
public class InvestorLoanTransactionDAO  extends AbstractDAO<InvestorLoanTransaction>{
	
	private static final Logger log = LoggerFactory.getLogger(InvestorLoanTransactionDAO.class);

	public List<InvestorLoanTransaction> getByAccountDate(LocalDateTime start, LocalDateTime end, String accountId) {
		TypedQuery<InvestorLoanTransaction> query =
				  getEntityManager().createNamedQuery("InvestorLoanTransaction.accountdate", InvestorLoanTransaction.class);
		query.setParameter("accountId", accountId);
		query.setParameter("start", Timestamp.valueOf(start));
		query.setParameter("end", Timestamp.valueOf(end));
		return query.getResultList();
	}

	public List<InvestorLoanTransaction> getByAccountDateBalFwd(LocalDateTime start, LocalDateTime end, String accountId) {
		TypedQuery<InvestorLoanTransaction> query =
				  getEntityManager().createNamedQuery("InvestorLoanTransaction.accountdatebalancefwd", InvestorLoanTransaction.class);
		query.setParameter("accountId", accountId);
		query.setParameter("start", Timestamp.valueOf(start));
		query.setParameter("end", Timestamp.valueOf(end));
		return query.getResultList();
	}
    /**
     * Build a balance forward record for this period and this accountId.
     * Scan for balance forward records in the transactions. We may find 0, 1 or 2 depending on if the process has run before this.
     * Then add up the transactions in this period including the first balance forward record if found, and save or update the
     * end balance forward record with the new totals.
     * 
     * @param start
     * @param end
     * @param accountId
     */
	@Transactional
    public void processBalanceForward(LocalDateTime start, LocalDateTime end, String accountId) {
    	
    	// The db calls still need to use the old Dates
    	Date startDate = Date.from(start.atZone(ZoneId.systemDefault()).toInstant());
    	Date endDate = Date.from(end.atZone(ZoneId.systemDefault()).toInstant());
    	Timestamp endTimestamp = Timestamp.valueOf(end);
    	
    	// the balfwdlist has the balance forward records already created for this
    	// period. We expect 0, 1 or 2, depending on if this process had been run before for the period
    	List<InvestorLoanTransaction> balfwdlist = getByAccountDateBalFwd(start,end,accountId);
    	int balFwdCount = balfwdlist.size();
    	boolean accumulating = false;
    	InvestorLoanTransaction iltTotals = new InvestorLoanTransaction();
    	ObjectDescriptor objectDescriptor = getObjectDescriptor();
    	log.debug("Account {} balFwdCount found {}",accountId,balFwdCount);
    	if (balFwdCount == 0) {
    		// If there were no balance forward records we have to go back to the beginning of time
    		// and sum all the transactions.
    		startDate = new Date(0L);
    		accumulating = true;
    	}
    	if (balFwdCount == 1) {
    		// We found one balance forward record.
    		InvestorLoanTransaction balfwd1 = balfwdlist.get(0);
    		// Here we figure out if it is the first one or the last one
    		LocalDateTime createdDate = balfwd1.getCreatedDate().toLocalDateTime();
    		if (createdDate.equals(start)) {
    			// it was the first one, we're fine
    			// The flag will ensure we ignore up to that first balfwd
    			accumulating = false;
    		} else if (createdDate.equals(end)) {
    			// it was the last one. Fake finding the first one
    			// and make it look like we found 2 and search from the beginning of time.
    			accumulating = true;
    			startDate = new Date(0L);
    		} else {
    			// we have a screw up
    			// The best we can do is assume we have none
    			accumulating = true;
    			startDate = new Date(0L);
    		}
    	}
    	log.debug("After adjustments: startDate {} balFwdCount {}",startDate,balFwdCount);
    	
    	List<InvestorLoanTransaction> list = getByAccountDate(start,end,accountId);
    	InvestorLoanTransaction secondBalfwd = null;
    	// sum all the summable things into the totals object
    	for (InvestorLoanTransaction ilt: list) {
			if (!accumulating) {
				if (ilt.getIltType() == ItemType.BALANCE_FORWARD) {
					// If this is the first balfwd then accumulate and flag
					if (ilt.getCreatedDate().toLocalDateTime().equals(start)) {
						objectDescriptor.accumulate(ilt,iltTotals);
						accumulating = true;
					}
				} else {
					// do not accumulate
				}
			} else {
				if (ilt.getIltType() == ItemType.BALANCE_FORWARD) {
					// If this is the last balfwd then save it for update (which will exit the loop)
					if (ilt.getCreatedDate().toLocalDateTime().equals(end)) {
						secondBalfwd = ilt;
					}
				} else {
					objectDescriptor.accumulate(ilt,iltTotals);
				}
			}
    		if (secondBalfwd != null) {
    			break;
    		}
    	}
    	if (secondBalfwd != null) {
    		// we have an end balance forward record so ensure it is updated
    		objectDescriptor.copy(iltTotals,secondBalfwd);
    		merge(secondBalfwd);
    		log.debug("Updated existing balfwd");
    	} else {
        	iltTotals.setAccountId(accountId);
        	iltTotals.setCreatedDate(endTimestamp);
        	iltTotals.setIltType(ItemType.BALANCE_FORWARD);
    		create(iltTotals);
    		log.debug("Created new balfwd");
    	}
    }

	public List<String> getAccountIds(LocalDateTime start,
			LocalDateTime end) {
    	
    	// The db calls still need to use the old Dates
		Timestamp startTimestamp = Timestamp.valueOf(start);
		Timestamp endTimestamp = Timestamp.valueOf(end);
    	
		Query query = getEntityManager().createNamedQuery("InvestorLoanTransaction.accountIds");
		query.setParameter("start", startTimestamp);
		query.setParameter("end", endTimestamp);
		List ret = query.getResultList();
		return ret;
	}
	

}