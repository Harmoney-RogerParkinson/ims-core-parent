/**
 * 
 */
package com.harmoney.ims.core.database;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.harmoney.ims.core.database.descriptors.ObjectDescriptor;
import com.harmoney.ims.core.instances.InvestorLoanTransaction;
import com.harmoney.ims.core.instances.ItemType;

/**
 * @author Roger Parkinson
 *
 */
@Repository
public class InvestorLoanTransactionDAO  extends AbstractTransactionDAO<InvestorLoanTransaction>{
	
	private static final Logger log = LoggerFactory.getLogger(InvestorLoanTransactionDAO.class);


}