/**
 * 
 */
package com.harmoney.ims.core.database;

import java.lang.reflect.ParameterizedType;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.harmoney.ims.core.database.descriptors.ObjectDescriptor;
import com.harmoney.ims.core.database.descriptors.ObjectDescriptorGenerator;
import com.harmoney.ims.core.database.descriptors.Result;
import com.harmoney.ims.core.instances.InvestorLoanTransaction;
import com.harmoney.ims.core.instances.ItemType;
import com.harmoney.ims.core.instances.Transaction;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.bind.XmlObject;

/**
 * @author Roger Parkinson
 *
 */
public abstract class AbstractDAO<T extends Transaction> {
	
	private static final Logger log = LoggerFactory.getLogger(AbstractDAO.class);
	
	/**
	 * These fields are in the Transaction object but they are not always supplied by Account and Investment Order
	 * So we use this to suppress unnecessary error messages. 
	 */
	private static final String IGNORE_NAMES = "Reverse_Rejected_Date__c,Account_ID__c,harMoney_Account_Number__c,loan__Account__c,CreatedDate";
	
	@Autowired ObjectDescriptorGenerator objectDescriptorGenerator;
	private ObjectDescriptor objectDescriptor;
	private Class<T> clazz;

	@PersistenceContext(unitName="com.harmoney.ims.core.instances")
	private EntityManager entityManager;
	private String byId;
	private String byIMSId;
	private String byAll;
	private String byAccountIds;
	private String byAccountdatebalancefwd;
	private String byAccountdate;
	
	@Transactional
	public boolean create(T target) {
		entityManager.persist(target);
		entityManager.flush();
		return true;
	}
	@Transactional
	public boolean createReversal(T target, T oldRecord) {
		objectDescriptor.negate(target);
		target.setId(null);
        target.setCreatedDate(new Timestamp(System.currentTimeMillis()));
        target.setReversedId(oldRecord.getImsid());
        entityManager.persist(target);
        oldRecord.setReversedId(target.getImsid());
        oldRecord.setReversedOrRejectedDate(target.getReversedOrRejectedDate());
        entityManager.flush();
        return true;
	}
	@Transactional(readOnly=true)
	public List<T> getAll()
	{
		TypedQuery<T> query =
				  entityManager.createNamedQuery(byAll, clazz);
		return query.getResultList();
	}

	@Transactional
	public boolean delete(T target)
	{
		getObjectDescriptor().negate(target);
        target.setCreatedDate(new Timestamp(System.currentTimeMillis()));
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
	/**
	 * Get the id value for this object. Assumes there is a single Id field, not a composite.
	 * 
	 * @param object
	 */
	public long getId(T object) {
		Assert.notNull(object,"object must not be null");
		return objectDescriptor.getId(object);
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
	@Transactional
	public T getByIMSId(Long imsid) {
		TypedQuery<T> query =
				  entityManager.createNamedQuery(byIMSId, clazz);
		query.setParameter("imsid", imsid);
		T existing = query.getSingleResult();
		return existing;
	}
	@Transactional
	public T getById(String id) {
		TypedQuery<T> query =
				  entityManager.createNamedQuery(byId, clazz);
		query.setParameter("id", id);
		T existing;
		try {
			existing = query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
		return existing;
	}
	
	@SuppressWarnings("unchecked")
	@PostConstruct
	public void init() {
	    clazz = (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		byId = clazz.getSimpleName()+".id";
		byIMSId = clazz.getSimpleName()+".imsid";
		byAll = clazz.getSimpleName()+".all";
		byAccountdatebalancefwd = clazz.getSimpleName()+".accountdatebalancefwd";
		byAccountdate = clazz.getSimpleName()+".accountdate";
		byAccountIds = clazz.getSimpleName()+".accountIds";
		
		objectDescriptor = objectDescriptorGenerator.build(clazz);
	}
	public Class<T> getClazz() {
		return clazz;
	}
	public String getSalesforceTableName() {
		return objectDescriptor.getSalesforceTableName();
	}
	public List<String> getSalesforceFields() {
		return objectDescriptor.getSalesForceFields();
	}
	public Result unpack(SObject sobject, T target) {
		Map<String, Object> fieldMap = new HashMap<>();
		for (String fieldName : getSalesforceFields()) {
			String[] fieldNames = StringUtils.delimitedListToStringArray(fieldName, ",");
			if (fieldNames.length > 1) {
				fieldNames[0].toString();
			}
			for (String name: fieldNames) {
				String fieldValue;
				try {
					fieldValue = extractValueFromSObject(sobject,name);
				} catch (Exception e) {
					if (!IGNORE_NAMES.contains(name)) {
						log.warn(e.getMessage());
					}
					continue;
				}
				fieldMap.put(name, fieldValue);
			}
		}
		if (!fieldMap.containsKey("Reverse_Rejected_Date__c")) {
			fieldMap.put("Reverse_Rejected_Date__c", null);
		}
		if (!fieldMap.containsKey("CreatedDate")) {
			fieldMap.put("CreatedDate", null);
		}
		return unpack(fieldMap,target);
	}
	private String extractValueFromSObject(SObject sobject, String name) {
		if (name.indexOf('.') == -1) {
			if (sobject.getChild(name) == null) {
				throw new RuntimeException("No field ["+name+"] found in SObject");
			}
			return (String)sobject.getField(name);
		}
		String split[] = StringUtils.split(name, ".");
		SObject f = (SObject)sobject.getField(split[0]);
		if (f == null) {
			return null;
		}
		XmlObject xmlObject = f.getChild(split[1]);
		if (xmlObject == null) {
			throw new RuntimeException("No field ["+split[1]+"] found in SObject");
		}
		return (String)xmlObject.getValue();
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
	public Result unpack(Map<String, Object> sobject) {
		T target;
		try {
			target = (T)clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		Result ret = objectDescriptor.unpack(sobject, target);
		create(target);
		return ret;
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
		query.setParameter("start", Timestamp.valueOf(start));
		query.setParameter("end", Timestamp.valueOf(end));
		return query.getResultList();
	}

	public List<T> getByAccountDateBalFwd(LocalDateTime start, LocalDateTime end, String accountId) {
		TypedQuery<T> query =
				  getEntityManager().createNamedQuery(byAccountdatebalancefwd, clazz);
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
    	Date startDate = Date.from(start.atZone(ZoneId.systemDefault()).toInstant());
    	Date endDate = Date.from(end.atZone(ZoneId.systemDefault()).toInstant());
    	Timestamp endTimestamp = Timestamp.valueOf(end);
    	
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
    		startDate = new Date(0L);
    		accumulating = true;
    	}
    	if (balFwdCount == 1) {
    		// We found one balance forward record.
    		T balfwd1 = balfwdlist.get(0);
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
    	
    	List<T> list = getByAccountDate(start,end,accountId);
    	log.debug("Starting account scan: startDate {} accumulating {} size {}",startDate,accumulating,list.size());

    	T secondBalfwd = null;
    	// sum all the summable things into the totals object
    	for (T ilt: list) {
			if (!accumulating) {
				if (ilt.getTxType() == ItemType.BALANCE_FORWARD) {
					// If this is the first balfwd then accumulate and flag
					if (ilt.getCreatedDate().toLocalDateTime().equals(start)) {
						objectDescriptor.accumulate(ilt,iltTotals);
						accumulating = true;
					}
				} else {
					// do not accumulate
				}
			} else {
				if (ilt.getTxType() == ItemType.BALANCE_FORWARD) {
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
		Timestamp startTimestamp = Timestamp.valueOf(start);
		Timestamp endTimestamp = Timestamp.valueOf(end);
    	
		Query query = getEntityManager().createNamedQuery(byAccountIds);
		query.setParameter("start", startTimestamp);
		query.setParameter("end", endTimestamp);
		@SuppressWarnings("unchecked")
		List<String> ret = query.getResultList();
		return ret;
	}
}
