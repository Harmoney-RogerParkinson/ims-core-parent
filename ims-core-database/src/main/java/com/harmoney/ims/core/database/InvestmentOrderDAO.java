/**
 * 
 */
package com.harmoney.ims.core.database;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.harmoney.ims.core.database.descriptors.ObjectDescriptor;
import com.harmoney.ims.core.database.descriptors.ObjectDescriptorGenerator;
import com.harmoney.ims.core.database.descriptors.Result;
import com.harmoney.ims.core.instances.Account;
import com.harmoney.ims.core.instances.InvestmentOrder;
import com.sforce.soap.partner.sobject.SObject;

/**
 * @author Roger Parkinson
 *
 */
@Repository
public class InvestmentOrderDAO {
	
	private static final Logger log = LoggerFactory.getLogger(InvestmentOrderDAO.class);

	@Autowired UnpackHelper unpackHelper;
	@Autowired ObjectDescriptorGenerator objectDescriptorGenerator;
	private ObjectDescriptor objectDescriptor;
	private Class<InvestmentOrder> clazz;

	@PersistenceContext(unitName="com.harmoney.ims.core.instances")
	private EntityManager entityManager;
	private String byId;
	private String byIMSId;
	private String byAll;

	@SuppressWarnings("unchecked")
	@PostConstruct
	public void init() {
	    clazz = InvestmentOrder.class;
		byId = clazz.getSimpleName()+".id";
		byIMSId = clazz.getSimpleName()+".imsid";
		byAll = clazz.getSimpleName()+".all";
		
		objectDescriptor = objectDescriptorGenerator.build(clazz);
	}
	public Class<InvestmentOrder> getClazz() {
		return clazz;
	}
	@Transactional(readOnly=true)
	public List<InvestmentOrder> getAll()
	{
		TypedQuery<InvestmentOrder> query =
				  entityManager.createNamedQuery(byAll, clazz);
		return query.getResultList();
	}
	@Transactional
	public InvestmentOrder getByIMSId(Long imsid) {
		TypedQuery<InvestmentOrder> query =
				  entityManager.createNamedQuery(byIMSId, clazz);
		query.setParameter("imsid", imsid);
		InvestmentOrder existing = query.getSingleResult();
		return existing;
	}
	@Transactional
	public InvestmentOrder getById(String id) {
		TypedQuery<InvestmentOrder> query =
				  entityManager.createNamedQuery(byId, clazz);
		query.setParameter("id", id);
		InvestmentOrder existing;
		try {
			existing = query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
		return existing;
	}
	@Transactional
	public boolean create(InvestmentOrder target) {
		entityManager.persist(target);
		entityManager.flush();
		return true;
	}
	public ObjectDescriptor getObjectDescriptor() {
		return objectDescriptor;
	}
	@Transactional
	public Result createOrUpdate(SObject sobject) {
		LocalDateTime lastModifiedDate = LocalDateTime.now();
		
		String id = (String)sobject.getField("Id");
		InvestmentOrder account = getById(id);
		Result result = null;
		if (account == null) {
			// new record
			account = new InvestmentOrder();
			result = unpackHelper.unpack(sobject, account,objectDescriptor);
			account.setLastModifiedDate(Timestamp.valueOf(lastModifiedDate));
			entityManager.persist(account);
		} else {
			result = unpackHelper.unpack(sobject, account,objectDescriptor);
			account.setLastModifiedDate(Timestamp.valueOf(lastModifiedDate));
		}
		entityManager.flush();
		return result;
	}

}