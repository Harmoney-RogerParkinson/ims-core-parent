package com.harmoney.ims.core.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.harmoney.ims.core.instances.InvestorLoanTransaction;

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
public class InvestorLoanTransactionIT {

	@Autowired private InvestorLoanTransactionDAO investorLoanTransactionDAO;
//	private Timestamp today = Timestamp.valueOf(LocalDateTime.of(LocalDate.of(2017, 2, 23), LocalTime.MIDNIGHT));
	private Calendar today = ConvertUtils.convertToCalendar(LocalDateTime.of(LocalDate.of(2017, 2, 23), LocalTime.MIDNIGHT));

	@Test
	public void createTransactionTest() {
		InvestorLoanTransaction ilt = new InvestorLoanTransaction();
		ilt.setId("ILT_100");
		ilt.setName("ILT_100");
		ilt.setNetAmount(new BigDecimal(123.456));
		ilt.setCreatedDate(today);
		ilt.setAccountId("XYZ");
		assertTrue(investorLoanTransactionDAO.create(ilt));
		
		List<InvestorLoanTransaction> transactions = investorLoanTransactionDAO.getAll();
		assertEquals(1,transactions.size());
		// These verify the named queries
		long imsid = transactions.get(0).getImsid();
		InvestorLoanTransaction ilt1 = investorLoanTransactionDAO.getByIMSId(imsid);
		assertEquals(imsid,ilt1.getImsid());
		String id = transactions.get(0).getId();
		InvestorLoanTransaction ilt2 = investorLoanTransactionDAO.getById(id);
		assertEquals(imsid,ilt2.getImsid());
		// verify the update
		ilt2.setName("ILT_101");
		assertTrue(investorLoanTransactionDAO.update(ilt2));
		InvestorLoanTransaction ilt3 = investorLoanTransactionDAO.getById(id);
		assertEquals("ILT_101",ilt3.getName());
		LocalTime t = LocalTime.MIDNIGHT;
		LocalDateTime start = LocalDateTime.of(LocalDate.of(2017, 2, 1), t);
		LocalDateTime end = LocalDateTime.of(LocalDate.of(2017, 2, 1).with(TemporalAdjusters.lastDayOfMonth()), t);
		List<String> accountIds = investorLoanTransactionDAO.getAccountIds(start,end);
		assertEquals(1,accountIds.size());
		transactions = investorLoanTransactionDAO.getAll();
		assertEquals(1,transactions.size());
	}
	
	@Test
	public void createReversalTransactionTest() {
		InvestorLoanTransaction ilt = new InvestorLoanTransaction();
		ilt.setId("ILT_150");
		ilt.setName("ILT_150");
		ilt.setNetAmount(new BigDecimal(123.456).setScale(2, BigDecimal.ROUND_HALF_DOWN));
		ilt.setCreatedDate(today);
		assertTrue(investorLoanTransactionDAO.create(ilt));
		
		ilt = new InvestorLoanTransaction();
		ilt.setId("ILT_150");
		ilt.setName("ILT_150");
		ilt.setNetAmount(new BigDecimal(123.456).setScale(2, BigDecimal.ROUND_HALF_DOWN));
		ilt.setCreatedDate(today);
		ilt.setReversed(true);
		ilt.setReversedOrRejectedDate(java.sql.Date.valueOf("2017-02-24"));
		assertTrue(investorLoanTransactionDAO.update(ilt));
		
		List<InvestorLoanTransaction> transactions = investorLoanTransactionDAO.getAll();
		assertEquals(2,transactions.size());
		assertEquals(new BigDecimal(-123.46).setScale(2, BigDecimal.ROUND_HALF_DOWN), transactions.get(0).getNetAmount());
		assertEquals(transactions.get(1).getReversedOrRejectedDate(), java.sql.Date.valueOf("2017-02-24"));
		assertEquals(transactions.get(1).getReversedId(), transactions.get(0).getImsid());
		assertEquals(transactions.get(0).getReversedId(), transactions.get(1).getImsid());
		
		// this should log a message refusing the update
		assertFalse(investorLoanTransactionDAO.update(transactions.get(1)));
	}
	
	@Test
	public void deleteTransactionTest() {
		InvestorLoanTransaction ilt = new InvestorLoanTransaction();
		ilt.setId("ILT_200");
		ilt.setName("ILT_200");
		ilt.setNetAmount(new BigDecimal(123.456));
		assertTrue(investorLoanTransactionDAO.create(ilt));

		ilt = new InvestorLoanTransaction();
		ilt.setId("ILT_200");
		ilt.setName("ILT_200");
		ilt.setNetAmount(new BigDecimal(123.456));
		assertTrue(investorLoanTransactionDAO.delete(ilt));
		
		List<InvestorLoanTransaction> transactions = investorLoanTransactionDAO.getAll();
		assertEquals(2,transactions.size());
	}
	
}
