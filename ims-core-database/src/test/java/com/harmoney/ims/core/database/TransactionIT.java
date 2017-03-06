package com.harmoney.ims.core.database;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.harmoney.ims.core.instances.IFT;
import com.harmoney.ims.core.instances.ILT;

/**
 * @author Roger Parkinson
 * 
 * Writes some test data to the database.
 * The data is not necessarily internally consistent.
 * This is an integration test (IT) because it requires a db connection.
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={DatabaseSpringConfig.class})
@Transactional // needed for rollback
@Rollback // rollback all db changes
public class TransactionIT {

	private static final Logger log = LoggerFactory
			.getLogger(TransactionIT.class);
	
	@Autowired private ILTDAO iltDAO;
	@Autowired private IFTDAO iftDAO;

	@Test
	public void createILTTest() {
		ILT ilt = new ILT();
		ilt.setId("ILT_100");
		ilt.setName("ILT_100");
		ilt.setNetAmount(new BigDecimal(123.456));
		ilt.setCreatedDate(java.sql.Date.valueOf("2017-02-23"));
		iltDAO.create(ilt);
		List<ILT> transactions = iltDAO.getAll();
		assertEquals(1,transactions.size());
		// These verify the named queries
		long imsid = transactions.get(0).getImsid();
		ILT ilt1 = iltDAO.getByIMSId(imsid);
		assertEquals(imsid,ilt1.getImsid());
		String id = transactions.get(0).getId();
		ILT ilt2 = iltDAO.getById(id);
		assertEquals(imsid,ilt2.getImsid());
		// verify the update
		ilt2.setName("ILT_101");
		iltDAO.upate(ilt2);
		ILT ilt3 = iltDAO.getById(id);
		assertEquals("ILT_101",ilt3.getName());
	}
	
	@Test
	public void createReversalTransactionTest() {
		ILT ilt = new ILT();
		ilt.setId("ILT_150");
		ilt.setName("ILT_150");
		ilt.setNetAmount(new BigDecimal(123.456).setScale(2, BigDecimal.ROUND_HALF_DOWN));
		ilt.setCreatedDate(java.sql.Date.valueOf("2017-02-23"));
		iltDAO.createReversal(ilt);
		List<ILT> transactions = iltDAO.getAll();
		assertEquals(1,transactions.size());
		assertEquals(new BigDecimal(-123.46).setScale(2, BigDecimal.ROUND_HALF_DOWN), transactions.get(0).getNetAmount());
	}
	
	@Test
	public void deleteILTTransactionTest() {
		ILT ilt = new ILT();
		ilt.setId("ILT_200");
		ilt.setName("ILT_200");
		ilt.setNetAmount(new BigDecimal(123.456));
		iltDAO.create(ilt);
		List<ILT> transactions = iltDAO.getAll();
		assertEquals(1,transactions.size());
		transactions.get(0);
		iltDAO.delete(transactions.get(0));
		transactions = iltDAO.getAll();
		assertEquals(0,transactions.size());
	}
	
	@Test
	public void createIFTTest() {
		IFT ift = new IFT();
		ift.setId("IFT_100");
		ift.setName("IFT_100");
		ift.setTransactionAmount(new BigDecimal(123.456));
		ift.setCreatedDate(java.sql.Date.valueOf("2017-02-23"));
		iftDAO.create(ift);
		List<IFT> transactions = iftDAO.getAll();
		assertEquals(1,transactions.size());
		// These verify the named queries
		long imsid = transactions.get(0).getImsid();
		IFT ift1 = iftDAO.getByIMSId(imsid);
		assertEquals(imsid,ift1.getImsid());
		String id = transactions.get(0).getId();
		IFT ift2 = iftDAO.getById(id);
		assertEquals(imsid,ift2.getImsid());
		// verify the update
		ift2.setName("IFT_101");
		iftDAO.upate(ift2);
		IFT ift3 = iftDAO.getById(id);
		assertEquals("IFT_101",ift3.getName());
	}
}
