package com.harmoney.ims.core.database;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.harmoney.ims.core.instances.InvestorFundTransaction;

@Repository
public class InvestorFundTransactionDAO extends AbstractDAO<InvestorFundTransaction> {

	private static final Logger log = LoggerFactory.getLogger(InvestorFundTransactionDAO.class);

	@Transactional
	public boolean delete(InvestorFundTransaction target)
	{
		getObjectDescriptor().negate(target);
        target.setCreatedDate(new Date());
        InvestorFundTransaction oldRecord = getById(target.getId());
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
	public boolean update(InvestorFundTransaction target)
	{
		InvestorFundTransaction oldRecord = getById(target.getId());
		if (oldRecord == null) {
			// updating a record we don't know about
			create(target);
			return false;
		}
		if (oldRecord.getReversedId() != 0) {
			// trying up update a reversed record
			log.error("Can't update a reversed record. Id={}",target.getId());
			return false;
		}
		if (target.isRejected() || target.isReversed()) {
			// reversing an existing unreversed record
			createReversal(target, oldRecord);
			return false;
		}
		update(target);
        return true;
	}

}
