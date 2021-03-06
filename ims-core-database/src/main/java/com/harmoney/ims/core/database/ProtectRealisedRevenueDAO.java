/**
 * 
 */
package com.harmoney.ims.core.database;

import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import com.harmoney.ims.core.instances.ProtectRealisedRevenue;

/**
 * @author Roger Parkinson
 *
 */
@Repository
public class ProtectRealisedRevenueDAO extends AbstractSimpleDAO<ProtectRealisedRevenue>{
	
	@Override
	protected void localInit() {
	}

	@Override
	protected void fieldUpdates(ProtectRealisedRevenue object) {
	}

	public ProtectRealisedRevenue getByInvestmentOrderIdAndDate(String investmentOrderId,
			Date dueDate) {
		TypedQuery<ProtectRealisedRevenue> query =
				  entityManager.createNamedQuery("ProtectRealisedRevenue.investmentOrderIdAndDueDate", ProtectRealisedRevenue.class);
		query.setParameter("investmentOrderId", investmentOrderId);
		query.setParameter("dueDate", dueDate);
		try {
			return query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public List<Date> getUniqueDueDatesUnsatisfied(String loanAccountId) {
		TypedQuery<Date> query =
				  entityManager.createNamedQuery("ProtectRealisedRevenue.uniqueDueDatesUnsatisfied", Date.class);
		query.setParameter("loanAccountId", loanAccountId);
		return query.getResultList();
	}

	public List<ProtectRealisedRevenue> getByLoanAccountId(String loanAccountId) {
		TypedQuery<ProtectRealisedRevenue> query =
				  entityManager.createNamedQuery("ProtectRealisedRevenue.loanAccountId", ProtectRealisedRevenue.class);
		query.setParameter("loanAccountId", loanAccountId);
		return query.getResultList();
	}

}