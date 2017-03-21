/**
 * 
 */
package com.harmoney.ims.core.database;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import com.harmoney.ims.core.database.descriptors.ObjectDescriptor;
import com.harmoney.ims.core.database.descriptors.Result;
import com.harmoney.ims.core.instances.ItemType;
import com.harmoney.ims.core.instances.Transaction;

/**
 * @author Roger Parkinson
 *
 */
public abstract class AbstractTransactionDAO<T extends Transaction> extends AbstractDAO<T> {
	
	private static final Logger log = LoggerFactory.getLogger(AbstractTransactionDAO.class);
	
	
	private String byAccountIds;
	private String byAccountdatebalancefwd;
	private String byAccountdate;
    /**
     * A few nanoseconds before midnight, should be the last thing that happens that day.
     * The database (actually the Calendar object) doesn't store precision more than 999000000, though Java default is 999999999
     */
    private static final LocalTime LAST_MOMENT = LocalTime.of(23, 59, 59, 999000000);
    
    public LocalDateTime getLastMomentOfLastMonth(LocalDate date) {
    	return date.minusMonths(1).with(TemporalAdjusters.lastDayOfMonth()).atTime(LAST_MOMENT);
    }
    public LocalDateTime getLastMomentOfMonth(LocalDate date) {
    	return date.with(TemporalAdjusters.lastDayOfMonth()).atTime(LAST_MOMENT);
    }
	
	@Transactional
	public boolean createReversal(T target, T oldRecord) {
		objectDescriptor.negate(target);
		target.setId(null);
        target.setCreatedDate(new GregorianCalendar());
        target.setReversedId(oldRecord.getImsid());
        entityManager.persist(target);
        oldRecord.setReversedId(target.getImsid());
        oldRecord.setReversedOrRejectedDate(target.getReversedOrRejectedDate());
        entityManager.flush();
        return true;
	}
	@Transactional
	public boolean delete(T target)
	{
		getObjectDescriptor().negate(target);
        target.setCreatedDate(new GregorianCalendar());
        T oldRecord = getById(target.getId());
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
	public boolean update(T target) {
		T oldRecord = getById(target.getId());
		if (oldRecord == null) {
			// updating a record we don't know about
			return create(target);
		}
		if (oldRecord.getReversedId() != 0) {
			// trying up update a reversed record
			log.error("Can't update a reversed record. Id={}",target.getId());
			return false;
		}
		if (target.getReversedOrRejectedDate() != null) {
			// reversing an existing unreversed record
			return createReversal(target, oldRecord);
		}
		target.setImsid(oldRecord.getImsid());
		getEntityManager().merge(target);
		getEntityManager().flush();
        return true;
	}
	@Transactional
	public boolean merge(T target) {
		getEntityManager().merge(target);
		getEntityManager().flush();
        return true;
	}
	
	protected void localInit() {
		byAccountdatebalancefwd = clazz.getSimpleName()+".accountdatebalancefwd";
		byAccountdate = clazz.getSimpleName()+".accountdate";
		byAccountIds = clazz.getSimpleName()+".accountIds";		
	}
	/**
	 * Unpack the values in the map into the fields in the given message.
	 * 
	 * @param message from pushTopic
	 * @param o (the target object)
	 * @return A Results object containing errors etc
	 */
	public Result unpackMessage(Map<String, Map<String, Object>> message, T o) {
		return objectDescriptor.unpack(message.get("sobject"), o);
	}
	/**
	 * Unpack the values in the map into the fields in the given sobject.
	 * 
	 * @param sobject
	 * @param o (the target object)
	 * @return A Results object containing errors etc
	 */
	public Result unpack(Map<String, Object> sobject, T o) {
		return objectDescriptor.unpack(sobject, o);
	}
	public ObjectDescriptor getObjectDescriptor() {
		return objectDescriptor;
	}
	protected EntityManager getEntityManager() {
		return entityManager;
	}
	
	// Balance forward handling

	public List<T> getByAccountDate(LocalDateTime start, LocalDateTime end, String accountId) {
		TypedQuery<T> query =
				  getEntityManager().createNamedQuery(byAccountdate, clazz);
		query.setParameter("accountId", accountId);
		query.setParameter("start", ConvertUtils.convertToCalendar(start));
		query.setParameter("end", ConvertUtils.convertToCalendar(end));
		return query.getResultList();
	}

	public List<T> getByAccountDateBalFwd(LocalDateTime start, LocalDateTime end, String accountId) {
		TypedQuery<T> query =
				  getEntityManager().createNamedQuery(byAccountdatebalancefwd, clazz);
		query.setParameter("accountId", accountId);
		query.setParameter("start", ConvertUtils.convertToCalendar(start));
		query.setParameter("end", ConvertUtils.convertToCalendar(end));
		return query.getResultList();
	}
    /**
     * Build a balance forward record for this period and this accountId.
     * Scan for balance forward records in the transactions. We may find 0, 1 or 2 depending on if the process has run before this.
     * Then add up the transactions in this period including the first balance forward record if found, and save or update the
     * end balance forward record with the new totals.
     * Return the total cound of balance forward records found in this period, including the ones we created. This is used for testing.
     * 
     * @param start
     * @param end
     * @param accountId
     * @return balFwdCount
     */
	@Transactional
    public int processBalanceForward(LocalDateTime start, LocalDateTime end, String accountId) {
    	
    	// The db calls still need to use the old Dates
//    	Date startDate = Date.from(start.atZone(ZoneId.systemDefault()).toInstant());
//    	Date endDate = Date.from(end.atZone(ZoneId.systemDefault()).toInstant());
    	Calendar endTimestamp = ConvertUtils.convertToCalendar(end);
    	
    	// the balfwdlist has the balance forward records already created for this
    	// period. We expect 0, 1 or 2, depending on if this process had been run before for the period
    	List<T> balfwdlist = getByAccountDateBalFwd(start,end,accountId);
    	int balFwdCount = balfwdlist.size();
    	boolean accumulating = false;
		T iltTotals;
		try {
			iltTotals = (T)clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}

    	ObjectDescriptor objectDescriptor = getObjectDescriptor();
    	log.debug("Initial balFwdCount: {} Account: {} ",balFwdCount,accountId);
    	if (balFwdCount == 0) {
    		// If there were no balance forward records we have to go back to the beginning of time
    		// and sum all the transactions.
    		start = LocalDateTime.of(1970, 1, 1, 0, 0);
    		accumulating = true;
    	}
    	if (balFwdCount == 1) {
    		// We found one balance forward record.
    		T balfwd1 = balfwdlist.get(0);
    		// Here we figure out if it is the first one or the last one
    		LocalDateTime createdDate = ConvertUtils.convertTolocalDateTime(balfwd1.getCreatedDate());
    		if (createdDate.equals(start)) {
    			// it was the first one, we're fine
    			// The flag will ensure we ignore up to that first balfwd
    			accumulating = false;
    		} else if (createdDate.equals(end)) {
    			// it was the last one. Fake finding the first one
    			// and make it look like we found 2 and search from the beginning of time.
    			accumulating = true;
    			start = LocalDateTime.of(1970, 1, 1, 0, 0);;
    		} else {
    			// we have a screw up
    			// The best we can do is assume we have none
    			accumulating = true;
    			start = LocalDateTime.of(1970, 1, 1, 0, 0);
    		}
    	}
    	
    	List<T> list = getByAccountDate(start,end,accountId);
    	log.debug("Starting account scan: startDate {} accumulating {} size {}",start,accumulating,list.size());

    	T secondBalfwd = null;
    	// sum all the summable things into the totals object
    	for (T ilt: list) {
			if (!accumulating) {
				if (ilt.getTxType() == ItemType.BALANCE_FORWARD) {
					// If this is the first balfwd then accumulate and flag
					if (ConvertUtils.convertTolocalDateTime(ilt.getCreatedDate()).equals(start)) {
						objectDescriptor.accumulate(ilt,iltTotals);
						accumulating = true;
					}
				} else {
					// do not accumulate
				}
			} else {
				if (ilt.getTxType() == ItemType.BALANCE_FORWARD) {
					// If this is the last balfwd then save it for update (which will exit the loop)
					if (ConvertUtils.convertTolocalDateTime(ilt.getCreatedDate()).equals(end)) {
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
    		log.debug("Updated existing balfwd accountId: {}",accountId);
    	} else {
        	iltTotals.setAccountId(accountId);
        	iltTotals.setCreatedDate(endTimestamp);
        	iltTotals.setTxType(ItemType.BALANCE_FORWARD);
    		create(iltTotals);
    		log.debug("Created new balfwd accountId: {}",accountId);
    		balFwdCount++;
    	}
    	log.debug("Final balFwdCount: {} Account: {} ",balFwdCount,accountId);

    	return balFwdCount;
    }

	public List<String> getAccountIds(LocalDateTime start,
			LocalDateTime end) {
    	
    	// The db calls still need to use the old Dates
		Calendar startTimestamp = ConvertUtils.convertToCalendar(start);
		Calendar endTimestamp = ConvertUtils.convertToCalendar(end);
    	
		Query query = getEntityManager().createNamedQuery(byAccountIds);
		query.setParameter("start", startTimestamp);
		query.setParameter("end", endTimestamp);
		@SuppressWarnings("unchecked")
		List<String> ret = query.getResultList();
		return ret;
	}
}
