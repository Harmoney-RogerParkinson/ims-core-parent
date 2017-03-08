/**
 * 
 */
package com.harmoney.ims.core.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.harmoney.ims.core.database.descriptors.Result;
import com.harmoney.ims.core.instances.InvestmentOrder;
import com.sforce.soap.partner.sobject.SObject;

/**
 * @author Roger Parkinson
 *
 */
@Repository
public class InvestmentOrderDAO  extends AbstractDAO<InvestmentOrder>{
	
	private static final Logger log = LoggerFactory.getLogger(InvestmentOrderDAO.class);

	public Result unpack(SObject sobject, InvestmentOrder investmentOrder) {
		// TODO Auto-generated method stub
		return null;
		
	}
	

}