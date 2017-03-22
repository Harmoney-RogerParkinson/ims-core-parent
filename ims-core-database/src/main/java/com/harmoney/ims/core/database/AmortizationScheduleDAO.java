/**
 * 
 */
package com.harmoney.ims.core.database;

import org.springframework.stereotype.Repository;

import com.harmoney.ims.core.instances.AmortizationSchedule;

/**
 * @author Roger Parkinson
 *
 */
@Repository
public class AmortizationScheduleDAO extends AbstractSimpleDAO<AmortizationSchedule>{
	
	@Override
	protected void localInit() {
	}
	@Override
	protected void fieldUpdates(AmortizationSchedule object) {
		
	}

}