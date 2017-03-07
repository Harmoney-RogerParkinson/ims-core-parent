/**
 * 
 */
package com.harmoney.ims.core.database;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.harmoney.ims.core.instances.InvestorLoanTransaction;

/**
 * @author Roger Parkinson
 *
 */
@Repository
public class InvestorLoanTransactionDAO  extends AbstractDAO<InvestorLoanTransaction>{
	
	private static final Logger log = LoggerFactory.getLogger(InvestorLoanTransactionDAO.class);
	
	@Transactional
	public boolean delete(InvestorLoanTransaction target)
	{
		getObjectDescriptor().negate(target);
        target.setCreatedDate(new Date());
        InvestorLoanTransaction oldRecord = getById(target.getId());
        if (oldRecord == null) {
        	// we don't know about this record. Can't delete it
        	log.error("Can't delete unknown record. Id={}",target.getId());
        	return false;
        }
        if (oldRecord.getReversedId() != 0) {
        	// Trying to delete a reversed transaction
        	log.error("Can't delete reversed record. Id={}",target.getId());
        	return false;
        }
        // Generate a reversal transaction and link the two together
        target.setReversedId(oldRecord.getImsid());
        target.setReversedOrRejectedDate(new Date());
        target.setId(null);
        getEntityManager().persist(target);
        oldRecord.setReversedId(target.getImsid());
        oldRecord.setReversedOrRejectedDate(target.getReversedOrRejectedDate());
        getEntityManager().flush();
        return true;
	}
	
	@Transactional
	public boolean update(InvestorLoanTransaction target)
	{
		InvestorLoanTransaction oldRecord = getById(target.getId());
		if (oldRecord == null) {
			// updating a record we don't know about
			return create(target);
		}
		if (oldRecord.getReversedId() != 0) {
			// trying up update a reversed record
			log.error("Can't update a reversed record. Id={}",target.getId());
			return false;
		}
		if (target.isRejected() || target.isReversed()) {
			// reversing an existing unreversed record
			return createReversal(target, oldRecord);
		}
		getEntityManager().merge(target);
		getEntityManager().flush();
        return true;
	}

	
}