/**
 * 
 */
package com.harmoney.ims.core.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.harmoney.ims.core.instances.AccountSummary;

/**
 * @author Roger Parkinson
 *
 */
@Repository
public class AccountSummaryDAO  extends AbstractDAO<AccountSummary>{
	
	private static final Logger log = LoggerFactory.getLogger(AccountSummaryDAO.class);

}