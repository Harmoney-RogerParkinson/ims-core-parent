/**
 * 
 */
package com.harmoney.ims.core.database;

import java.time.LocalDateTime;
import java.util.Calendar;

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
}