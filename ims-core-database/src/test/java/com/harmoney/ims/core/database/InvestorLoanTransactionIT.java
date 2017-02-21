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

	private static final Logger log = LoggerFactory
			.getLogger(InvestorLoanTransactionIT.class);
	
	@Autowired private InvestorLoanTransactionDAO investorLoanTransactionDAO;

	@Test
	public void createTransactionTest() {
		InvestorLoanTransaction ilt = new InvestorLoanTransaction();
		ilt.setName("ILT_100");
		ilt.setNetAmount(new BigDecimal(123.456));
		investorLoanTransactionDAO.createTransaction(ilt);
		List<InvestorLoanTransaction> transactions = investorLoanTransactionDAO.getAllTransactions();
		assertEquals(1,transactions.size());
	}
	
	@Test
	public void deleteTransactionTest() {
		InvestorLoanTransaction ilt = new InvestorLoanTransaction();
		ilt.setName("ILT_200");
		ilt.setNetAmount(new BigDecimal(123.456));
		investorLoanTransactionDAO.createTransaction(ilt);
		List<InvestorLoanTransaction> transactions = investorLoanTransactionDAO.getAllTransactions();
		assertEquals(1,transactions.size());
		transactions.get(0);
		investorLoanTransactionDAO.deleteTransaction(transactions.get(0));
		transactions = investorLoanTransactionDAO.getAllTransactions();
		assertEquals(0,transactions.size());
	}
	
}
