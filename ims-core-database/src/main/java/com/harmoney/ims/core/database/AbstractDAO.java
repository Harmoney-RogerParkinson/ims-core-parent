/**
 * 
 */
package com.harmoney.ims.core.database;

import java.lang.reflect.ParameterizedType;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * @author Roger Parkinson
 *
 */
abstract class AbstractDAO<T> {
	
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
	public void createTransaction(T target) {
		entityManager.persist(target);
		entityManager.flush();
	}
	@Transactional
	public void createReversalTransaction(T target) {
		objectDescriptor.negate(target);
		createTransaction(target);
	}
	@Transactional(readOnly=true)
	public List<T> getAllTransactions()
	{
		TypedQuery<T> query =
				  entityManager.createNamedQuery(byAll, clazz);
		return query.getResultList();
	}

	@Transactional
	public void deleteTransaction(T target)
	{
		entityManager.remove(target);
		entityManager.flush();
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
	public void upateTransaction(T target) {
		entityManager.merge(target);
		entityManager.flush();
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
		T existing = query.getSingleResult();
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

}
