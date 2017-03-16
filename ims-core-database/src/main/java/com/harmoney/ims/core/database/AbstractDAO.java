package com.harmoney.ims.core.database;

import java.lang.reflect.ParameterizedType;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.harmoney.ims.core.database.descriptors.ObjectDescriptor;
import com.harmoney.ims.core.database.descriptors.ObjectDescriptorGenerator;

abstract class AbstractDAO<T> {

	@Autowired ObjectDescriptorGenerator objectDescriptorGenerator;
	protected ObjectDescriptor objectDescriptor;
	protected Class<T> clazz;

	@PersistenceContext(unitName="com.harmoney.ims.core.instances")
	protected EntityManager entityManager;
	private String byId;
	private String byIMSId;
	private String byAll;
	
	@SuppressWarnings("unchecked")
	@PostConstruct
	public void init() {
	    clazz = (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		byId = clazz.getSimpleName()+".id";
		byIMSId = clazz.getSimpleName()+".imsid";
		byAll = clazz.getSimpleName()+".all";
		
		objectDescriptor = objectDescriptorGenerator.build(clazz);
		localInit();
	}
	abstract protected void localInit();

	@Transactional(readOnly=true)
	public List<T> getAll()
	{
		TypedQuery<T> query =
				  entityManager.createNamedQuery(byAll, clazz);
		return query.getResultList();
	}
	
	@Transactional
	public T getByIMSId(Long imsid) {
		TypedQuery<T> query =
				  entityManager.createNamedQuery(byIMSId, clazz);
		query.setParameter("imsid", imsid);
		T existing = query.getSingleResult();
		return existing;
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
	@Transactional
	public boolean create(T target) {
		entityManager.persist(target);
		entityManager.flush();
		return true;
	}
	public String getSalesforceTableName() {
		return objectDescriptor.getSalesforceTableName();
	}
	public List<String> getSalesforceFields() {
		return objectDescriptor.getSalesForceFields();
	}
	

}
