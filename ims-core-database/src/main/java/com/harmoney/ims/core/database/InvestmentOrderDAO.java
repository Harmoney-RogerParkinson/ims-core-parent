/**
 * 
 */
package com.harmoney.ims.core.database;

import java.time.LocalDateTime;
import java.util.Calendar;

import org.springframework.stereotype.Repository;

import com.harmoney.ims.core.instances.InvestmentOrder;

/**
 * @author Roger Parkinson
 *
 */
@Repository
public class InvestmentOrderDAO extends AbstractSimpleDAO<InvestmentOrder>{
	
	@Override
	protected void localInit() {
	}
	@Override
	protected void fieldUpdates(InvestmentOrder object) {
		LocalDateTime lastModifiedDate = LocalDateTime.now();
		Calendar calendar = ConvertUtils.convertToCalendar(lastModifiedDate);
		object.setLastModifiedDate(calendar);
	}

}