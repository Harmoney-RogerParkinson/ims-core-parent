/**
 * 
 */
package com.harmoney.ims.core.database;

import java.time.LocalDateTime;
import java.util.Calendar;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.harmoney.ims.core.instances.Account;
import com.harmoney.ims.core.instances.LoanAccount;

/**
 * @author Roger Parkinson
 *
 */
@Repository
public class LoanAccountDAO extends AbstractSimpleDAO<LoanAccount>{
	
	@Override
	protected void localInit() {
	}
	@Transactional(readOnly=true)
	public Account getByHarmoneyAccountNumber(String harmoneyAccountNumber) throws NoResultException {
		TypedQuery<Account> query =
				  entityManager.createNamedQuery("LoanAccount.harmoneyAccountNumber", Account.class);
		query.setParameter("harmoneyAccountNumber", harmoneyAccountNumber);
		return query.getSingleResult();
	}

	@Override
	protected void fieldUpdates(LoanAccount object) {
		LocalDateTime lastModifiedDate = LocalDateTime.now();
		Calendar calendar = ConvertUtils.convertToCalendar(lastModifiedDate);
		object.setLastModifiedDate(calendar);
	}
}