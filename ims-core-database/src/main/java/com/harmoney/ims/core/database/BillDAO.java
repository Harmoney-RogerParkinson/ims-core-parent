/**
 * 
 */
package com.harmoney.ims.core.database;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import com.harmoney.ims.core.instances.Bill;

/**
 * @author Roger Parkinson
 *
 */
@Repository
public class BillDAO extends AbstractSimpleDAO<Bill>{
	
	@Override
	protected void localInit() {
	}

	@Override
	protected void fieldUpdates(Bill object) {
		LocalDateTime lastModifiedDate = LocalDateTime.now();
		Calendar calendar = ConvertUtils.convertToCalendar(lastModifiedDate);
		object.setLastModifiedDate(calendar);
	}

	public List<Bill> getByLoanAccountId(String loanAccountId) {
		TypedQuery<Bill> query =
				  entityManager.createNamedQuery("Bill.loanAccountId", Bill.class);
		query.setParameter("loanAccountId", loanAccountId);
		return query.getResultList();
	}

	public Bill getByLoanAccountIdDueDate(String loanAccountId, Date dueDate) {
		TypedQuery<Bill> query =
				  entityManager.createNamedQuery("Bill.loanAccountIdDueDate", Bill.class);
		query.setParameter("loanAccountId", loanAccountId);
		query.setParameter("dueDate", dueDate);
		try {
			return query.getSingleResult();
		} catch (NoResultException  e) {
			return null;
		}
	}
}