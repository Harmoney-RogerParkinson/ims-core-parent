/**
 * 
 */
package com.harmoney.ims.core.database;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.harmoney.ims.core.database.descriptors.ObjectDescriptor;
import com.harmoney.ims.core.database.descriptors.ObjectDescriptorGenerator;
import com.harmoney.ims.core.instances.Account;

/**
 * @author Roger Parkinson
 *
 */
@Repository
public class AccountDAO {
	
	private static final Logger log = LoggerFactory.getLogger(AccountDAO.class);

	@Autowired ObjectDescriptorGenerator objectDescriptorGenerator;
	private ObjectDescriptor objectDescriptor;
	private Class<Account> clazz;

	@PersistenceContext(unitName="com.harmoney.ims.core.instances")
	private EntityManager entityManager;
	private String byId;
	private String byIMSId;
	private String byAll;

	@SuppressWarnings("unchecked")
	@PostConstruct
	public void init() {
	    clazz = Account.class;
		byId = clazz.getSimpleName()+".id";
		byIMSId = clazz.getSimpleName()+".imsid";
		byAll = clazz.getSimpleName()+".all";
		
		objectDescriptor = objectDescriptorGenerator.build(clazz);
	}
	public Class<Account> getClazz() {
		return clazz;
	}
	@Transactional
	public boolean create(Account target) {
		entityManager.persist(target);
		entityManager.flush();
		return true;
	}
	public ObjectDescriptor getObjectDescriptor() {
		return objectDescriptor;
	}
}