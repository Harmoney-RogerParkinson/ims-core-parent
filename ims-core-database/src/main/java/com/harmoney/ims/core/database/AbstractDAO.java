/**
 * 
 */
package com.harmoney.ims.core.database;

import java.lang.reflect.ParameterizedType;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
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
import com.harmoney.ims.core.instances.Transaction;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.bind.XmlObject;

/**
 * @author Roger Parkinson
 *
 */
public abstract class AbstractDAO<T extends Transaction> {
	
	private static final Logger log = LoggerFactory.getLogger(AbstractDAO.class);
	
	@Autowired ObjectDescriptorGenerator objectDescriptorGenerator;
	private ObjectDescriptor objectDescriptor;
	private Class<T> clazz;

	@PersistenceContext(unitName="com.harmoney.ims.core.instances")
	private EntityManager entityManager;
	private String byId;
	private String byIMSId;
	private String byAll;
	
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
        target.setCreatedDate(new Date());
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
        target.setCreatedDate(new Date());
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
					log.error(e.getMessage());
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

}
