package com.harmoney.ims.core.balanceforward;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.harmoney.ims.core.database.DatabaseSpringConfig;
import com.harmoney.ims.core.database.InvestorFundTransactionDAO;
import com.harmoney.ims.core.databaseloader.DatabaseLoader;
import com.harmoney.ims.core.databaseloader.DatabaseLoaderSpringConfig;
import com.harmoney.ims.core.instances.InvestorFundTransaction;
import com.harmoney.ims.core.partner.PartnerConnectionSpringConfig;
import com.harmoney.ims.core.queries.QuerySpringConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource("/H2Test.properties")
@ContextConfiguration(classes = { QuerySpringConfig.class, PartnerConnectionSpringConfig.class,DatabaseLoaderSpringConfig.class,DatabaseSpringConfig.class})
@ActiveProfiles("queue-handler-dev")
public class InvestorFundTransactionBalanceForwardTest {

    private static final Logger log = LoggerFactory.getLogger(InvestorFundTransactionBalanceForwardTest.class);

	@Autowired InvestorFundTransactionBalanceForward investorFundTransactionBalanceForward;
	@Autowired InvestorFundTransactionDAO investorFundTransactionDAO;
	@Autowired DatabaseLoader databaseLoader;
    private static final String dbLocation = "balanceforward.xml";

	@Test
	public void processBalanceForward() throws Exception {
		
		databaseLoader.loadDatabase(dbLocation);
		investorFundTransactionBalanceForward.setTestMode(true);
		
		LocalDate period1 = LocalDate.of(2017, 2, 15);
		LocalDate period2 = LocalDate.of(2017, 3, 15);

		Map<String,BigDecimal> netAmountByAccountIdPeriod1 = new HashMap<>();
		Map<String,BigDecimal> netAmountByAccountIdPeriod2 = new HashMap<>();

		// Run the first period. Should be three account ids and no existing balfwds, so we create three.
		processPeriod(period1, netAmountByAccountIdPeriod1, true);
		// Run the first period again. Should update the three balfwds with the same amount, not create new ones.
		processPeriod(period1, netAmountByAccountIdPeriod1, false);
		
		// now run the next period. That should result 5 account ids.
		// The three previous ones have no txns so we just create a balfwd in each of them with the same total as the prev period
		// The two new ones create one new balfwd each
		processPeriod(period2, netAmountByAccountIdPeriod2, true);
		// rerun period 2 and verify the balance forwards and totals are the same
		processPeriod(period2,  netAmountByAccountIdPeriod2, false);

		// Rerun all the periods (again)
		// verify the balfwd counts and the final totals do not change
		processPeriod(period1, netAmountByAccountIdPeriod1, false);
		processPeriod(period2, netAmountByAccountIdPeriod2, false);
	}
	
	/**
	 * Runs the balance forward processing for the period contained by the date given.
	 * The object returned is a summary of the accounts processed and their balfwd totals
	 * (the balfwd totals is suppressed unless we are in test mode).
	 * Analyse that the counts of balfwd records found on the database matches the number the process thing=ks there are
	 * and, depending on our mode (put is true or false) store the current final balfwd value or compare the
	 * final value with the one previously put.
	 * 
	 * @param periodDate
	 * @param netAmountByAccountId
	 * @param put
	 */
	private void processPeriod(LocalDate periodDate, Map<String,BigDecimal> netAmountByAccountId, boolean put) {
		
		log.debug("*** Processing Period {} {}",periodDate,put);

		BalanceForwardDTO balanceForwardDTO = investorFundTransactionBalanceForward.processBalanceForward(periodDate);

		if (!put) {
			log.debug("Checking accounts");
			for (Map.Entry<String, BigDecimal> entry: netAmountByAccountId.entrySet()) {
				log.debug("AccountId: {} amount: {}",entry.getKey(),entry.getValue());
			}
		}
		log.debug("Account checking complete\n");
		for (String accountId: balanceForwardDTO.getAccountIds()) {
			List<InvestorFundTransaction> balfwdlist = investorFundTransactionDAO.getByAccountDateBalFwd(
					balanceForwardDTO.getStart(),
					balanceForwardDTO.getEnd(),
					accountId
					);
			int s = balfwdlist.size();
			assertEquals(balanceForwardDTO.get(accountId),s);
			if (put) {
				netAmountByAccountId.put(accountId, balfwdlist.get(s-1).getTransactionAmount());
			} else {
				log.debug("AccountId: {} amount: {}",netAmountByAccountId.get(accountId),balfwdlist.get(s-1).getTransactionAmount());
				dumpTransactions(accountId,balanceForwardDTO);
				assertEquals(netAmountByAccountId.get(accountId),balfwdlist.get(s-1).getTransactionAmount());
			}
		}
	}
	
	private void dumpTransactions(String accountId, BalanceForwardDTO balanceForwardDTO) {
//		List<InvestorFundTransaction> ifts = investorFundTransactionDAO.getByAccountDate(balanceForwardDTO.getStart(), balanceForwardDTO.getEnd(), accountId);
//		log.debug("Dumping transactions {} {} {}",accountId,balanceForwardDTO.getStart(),balanceForwardDTO.getEnd());
//		for (InvestorFundTransaction ift: ifts) {
//			log.debug("Account: {} type: {} date: {}: amount: {}",
//					ift.getAccountId(), ift.getTxType(), ConvertUtils.convertTolocalDateTime(ift.getCreatedDate()),ift.getTransactionAmount());
//		}
	}

}
