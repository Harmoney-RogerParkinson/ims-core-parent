/**
 * 
 */
package com.harmoney.ims.core.database;

import java.util.Date;

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
}