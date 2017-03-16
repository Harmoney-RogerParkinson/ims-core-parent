/**
 * 
 */
package com.harmoney.ims.core.database;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.harmoney.ims.core.database.descriptors.ObjectDescriptor;
import com.harmoney.ims.core.database.descriptors.ObjectDescriptorGenerator;
import com.harmoney.ims.core.database.descriptors.Result;
import com.harmoney.ims.core.instances.InvestmentOrder;
import com.sforce.soap.partner.sobject.SObject;

/**
 * @author Roger Parkinson
 *
 */
@Repository
public class InvestmentOrderDAO extends AbstractDAO<InvestmentOrder>{
	
	private static final Logger log = LoggerFactory.getLogger(InvestmentOrderDAO.class);

	@Autowired UnpackHelper unpackHelper;

	@Transactional
	public Result createOrUpdate(SObject sobject) {
		LocalDateTime lastModifiedDate = LocalDateTime.now();
		
		String id = (String)sobject.getField("Id");
		InvestmentOrder account = getById(id);
		Result result = null;
		if (account == null) {
			// new record
			account = new InvestmentOrder();
			result = unpackHelper.unpack(sobject, account,objectDescriptor);
			account.setLastModifiedDate(Timestamp.valueOf(lastModifiedDate));
			entityManager.persist(account);
		} else {
			result = unpackHelper.unpack(sobject, account,objectDescriptor);
			account.setLastModifiedDate(Timestamp.valueOf(lastModifiedDate));
		}
		entityManager.flush();
		return result;
	}
	@Override
	protected void localInit() {
	}

}