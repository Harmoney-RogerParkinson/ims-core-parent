/**
 * 
 */
package com.harmoney.ims.core.database;

import java.lang.reflect.Method;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.harmoney.ims.core.instances.InvestorLoanTransaction;

/**
 * @author Roger Parkinson
 *
 */
@Repository
public class InvestorLoanTransactionDAO {
	
	private static final Logger log = LoggerFactory.getLogger(InvestorLoanTransactionDAO.class);
	@Autowired ObjectDescriptorGenerator objectDescriptorGenerator;
	private ObjectDescriptor objectDescriptor;

	@PersistenceContext(unitName="com.harmoney.ims.core.instances")
	private EntityManager entityManager;
	
	@Transactional
	public void createTransaction(InvestorLoanTransaction investorLoanTransaction) {
		entityManager.persist(investorLoanTransaction);
		entityManager.flush();
	}
	@Transactional(readOnly=true)
	public List<InvestorLoanTransaction> getAllTransactions()
	{
		return entityManager.createQuery("from com.harmoney.ims.core.instances.InvestorLoanTransaction", InvestorLoanTransaction.class).getResultList();
	}

	@Transactional
	public void deleteTransaction(InvestorLoanTransaction investorLoanTransaction)
	{
		entityManager.remove(investorLoanTransaction);
	}
	/**
	 * Get the id value for this object. Assumes there is a single Id field, not a composite.
	 * 
	 * @param object
	 */
	public Object getId(Object object) {
		Assert.notNull(object,"object must not be null");
		return objectDescriptor.getId(object);
	}
	@Transactional
	public void createReversalTransaction(InvestorLoanTransaction target) {
		objectDescriptor.negate(target);
		createTransaction(target);
	}
	@Transactional
	public void upateTransaction(InvestorLoanTransaction target) {
		InvestorLoanTransaction existing = entityManager.createQuery("from com.harmoney.ims.core.instances.InvestorLoanTransaction where id="+target.getId(), InvestorLoanTransaction.class).getSingleResult();
		target.setImsid(existing.getImsid());
		// TODO: verify update handling in JPA
		entityManager.merge(target);
		entityManager.flush();
	}
	
	@PostConstruct
	public void init() {
		objectDescriptor = objectDescriptorGenerator.build(InvestorLoanTransaction.class);
	}

}
