/**
 * 
 */
package com.harmoney.ims.core.database;

import java.time.LocalDateTime;
import java.util.Calendar;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.harmoney.ims.core.database.descriptors.Result;
import com.harmoney.ims.core.instances.Account;
import com.sforce.soap.partner.sobject.SObject;

/**
 * @author Roger Parkinson
 *
 */
@Repository
public class AccountDAO extends AbstractSimpleDAO<Account>{
	
	@Override
	protected void localInit() {
	}
	@Transactional(readOnly=true)
	public Account getByHarmoneyAccountNumber(String harmoneyAccountNumber) throws NoResultException {
		TypedQuery<Account> query =
				  entityManager.createNamedQuery("Account.harmoneyAccountNumber", Account.class);
		query.setParameter("harmoneyAccountNumber", harmoneyAccountNumber);
		return query.getSingleResult();
	}

	@Override
	protected void fieldUpdates(Account object) {
		LocalDateTime lastModifiedDate = LocalDateTime.now();
		Calendar calendar = ConvertUtils.convertToCalendar(lastModifiedDate);
		object.setLastModifiedDate(calendar);
	}
}