/**
 * 
 */
package com.harmoney.ims.core.database;

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

	public List<InvestorLoanTransaction> getByAccountDate(Date start, Date end, String accountId) {
		TypedQuery<InvestorLoanTransaction> query =
				  getEntityManager().createNamedQuery("InvestorLoanTransaction.accountdate", InvestorLoanTransaction.class);
		query.setParameter("accountId", accountId);
		query.setParameter("start", start);
		query.setParameter("end", end);
		return query.getResultList();
	}

	public List<InvestorLoanTransaction> getByAccountDateBalFwd(Date start,	Date end, String accountId) {
		TypedQuery<InvestorLoanTransaction> query =
				  getEntityManager().createNamedQuery("InvestorLoanTransaction.accountdatebalancefwd", InvestorLoanTransaction.class);
		query.setParameter("accountId", accountId);
		query.setParameter("start", start);
		query.setParameter("end", end);
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
    	
    	// the balfwdlist has the balance forward records already created for this
    	// period. We expect 0, 1 or 2, depending on if this process had been run before for the period
    	List<InvestorLoanTransaction> balfwdlist = getByAccountDateBalFwd(startDate,endDate,accountId);
    	int balFwdCount = balfwdlist.size();
    	boolean foundFirstBalfwd = false;
    	if (balFwdCount == 0) {
    		// If there were no balance forward records we have to go back to the beginning of time
    		// and sum all the transactions.
    		startDate = Date.from(LocalDateTime.MIN.atZone(ZoneId.systemDefault()).toInstant());
    	}
    	if (balFwdCount == 1) {
    		// We found one balance forward record.
    		// Here we figure out if it is the first one or the last one
    		LocalDateTime createdDate = LocalDateTime.ofInstant(balfwdlist.get(0).getCreatedDate().toInstant(), ZoneId.systemDefault());
    		if (createdDate.equals(start)) {
    			// it was the first one, we're fine
    		} else if (createdDate.equals(end)) {
    			// it was the last one. Fake finding the first one
    			// and make it look like we found 2
    			foundFirstBalfwd = true;
    			balFwdCount = 2;
    		} else {
    			// we have a screw up
    			// The best we can do is assume we have start and end
    			balFwdCount = 2;
    		}
    	}
    	
    	List<InvestorLoanTransaction> list = getByAccountDate(startDate,endDate,accountId);
    	InvestorLoanTransaction iltTotals = new InvestorLoanTransaction();
    	InvestorLoanTransaction secondBalfwd = null;
    	// sum all the summable things into the totals object
    	ObjectDescriptor objectDescriptor = getObjectDescriptor();
    	for (InvestorLoanTransaction ilt: list) {
    		switch (balFwdCount) {
    		case 0:
    			// No balance forward records found. Just accumulate what we find.
   				if (ilt.getIltType() == ItemType.BALANCE_FORWARD) {
   					// ... but if we do find a balance forward then ignore it
   				} else {
   					objectDescriptor.accumulate(ilt,iltTotals);
   				}
    			break;
    		case 1:
    			// Found one, and we already established it is the first one
    			if (!foundFirstBalfwd) {
    				if (ilt.getIltType() == ItemType.BALANCE_FORWARD) {
    					objectDescriptor.accumulate(ilt,iltTotals);
    					foundFirstBalfwd = true;
    				} else {
    					objectDescriptor.accumulate(ilt,iltTotals);
    				}
    			}
    			break;
    		case 2:
    			// Found both start and end. Ignore everything before the first one
    			// and stop before totaling the last one.
    			if (!foundFirstBalfwd) {
    				if (ilt.getIltType() == ItemType.BALANCE_FORWARD) {
    					objectDescriptor.accumulate(ilt,iltTotals);
    					foundFirstBalfwd = true;
    				}
    			} else {
    				if (ilt.getIltType() == ItemType.BALANCE_FORWARD) {
    					secondBalfwd = ilt;
    				} else {
    					objectDescriptor.accumulate(ilt,iltTotals);
    				}
    			}
    		}
    		if (secondBalfwd != null) {
    			break;
    		}
    	}
    	iltTotals.setAccountId(accountId);
    	iltTotals.setCreatedDate(endDate);
    	iltTotals.setIltType(ItemType.BALANCE_FORWARD);
    	if (secondBalfwd != null) {
    		// we have an end balance forward record so ensure it is updated
    		iltTotals.setImsid(secondBalfwd.getImsid());
    	}
    	merge(iltTotals);
    }

	public List<String> getAccountIds(LocalDateTime start,
			LocalDateTime end) {
    	
    	// The db calls still need to use the old Dates
    	Date startDate = Date.from(start.atZone(ZoneId.systemDefault()).toInstant());
    	Date endDate = Date.from(end.atZone(ZoneId.systemDefault()).toInstant());
    	
		Query query = getEntityManager().createNamedQuery("InvestorLoanTransaction.accountIds");
		query.setParameter("start", startDate);
		query.setParameter("end", endDate);
		List ret = query.getResultList();
		return ret;
	}
	

}