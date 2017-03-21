/**
 * 
 */
package com.harmoney.ims.core.database;

import java.time.LocalDateTime;
import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.harmoney.ims.core.database.descriptors.Result;
import com.harmoney.ims.core.instances.InvestmentOrder;
import com.sforce.soap.partner.sobject.SObject;

/**
 * @author Roger Parkinson
 *
 */
@Repository
public class InvestmentOrderDAO extends AbstractDAO<InvestmentOrder>{
	
	@Autowired UnpackHelper unpackHelper;

	@Transactional
	public Result createOrUpdate(SObject sobject) {
		LocalDateTime lastModifiedDate = LocalDateTime.now();
		
		String id = (String)sobject.getField("Id");
		InvestmentOrder account = getById(id);
		Calendar calendar = ConvertUtils.convertToCalendar(lastModifiedDate);
		Result result = null;
		if (account == null) {
			// new record
			account = new InvestmentOrder();
			result = unpackHelper.unpack(sobject, account,objectDescriptor);
			account.setLastModifiedDate(calendar);
			entityManager.persist(account);
		} else {
			result = unpackHelper.unpack(sobject, account,objectDescriptor);
			account.setLastModifiedDate(calendar);
		}
		entityManager.flush();
		return result;
	}
	@Override
	protected void localInit() {
	}

}