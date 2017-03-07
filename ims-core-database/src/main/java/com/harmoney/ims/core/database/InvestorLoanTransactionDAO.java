/**
 * 
 */
package com.harmoney.ims.core.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.harmoney.ims.core.instances.InvestorLoanTransaction;

/**
 * @author Roger Parkinson
 *
 */
@Repository
public class InvestorLoanTransactionDAO  extends AbstractDAO<InvestorLoanTransaction>{
	
	private static final Logger log = LoggerFactory.getLogger(InvestorLoanTransactionDAO.class);
	

}