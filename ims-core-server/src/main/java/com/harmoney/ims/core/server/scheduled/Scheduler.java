/**
 * 
 */
package com.harmoney.ims.core.server.scheduled;

import java.time.LocalDate;

import net.javacrumbs.shedlock.core.SchedulerLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.harmoney.ims.core.balanceforward.InvestorFundTransactionBalanceForward;
import com.harmoney.ims.core.balanceforward.InvestorLoanTransactionBalanceForward;
import com.harmoney.ims.core.partner.PartnerConnectionWrapper;
import com.harmoney.ims.core.queries.AccountQuery;
import com.harmoney.ims.core.queries.InvestmentOrderQuery;
import com.sforce.ws.ConnectionException;

/**
 * Schedules various background processes to run.
 * Note that all servers will run these processes at the scheduled time
 * hence the Schedlock to prevent more than one running.
 * 
 * @author Roger Parkinson
 *
 */
@Component
public class Scheduler {

    private static final Logger log = LoggerFactory.getLogger(Scheduler.class);

	@Autowired AccountQuery accountQuery;
	@Autowired PartnerConnectionWrapper partnerConnection;
	@Autowired InvestmentOrderQuery investmentOrderQuery;
	@Autowired InvestorLoanTransactionBalanceForward investorLoanTransactionBalanceForward;
	@Autowired InvestorFundTransactionBalanceForward investorFundTransactionBalanceForward;

	/**
	 * Run the query processes. By default these run daily
	 * at 01:00 AM using Pacific/Auckland time.
	 */
	@Scheduled(
			cron="${com.harmoney.ims.core.server.web.Scheduler.queries:0 0 1 * * *}",
			zone="${com.harmoney.ims.core.server.web.Scheduler.tz:Pacific/Auckland}"
			)
	@SchedulerLock(name = "scheduledQueries")
	public void runQueries() {
		log.info("starting scheduledQueries");
		try {
			try {
				accountQuery.doQuery();
			} catch (ConnectionException e) {
				log.error(e.getMessage(),e);
			}
			try {
				investmentOrderQuery.doQuery();
			} catch (ConnectionException e) {
				log.error(e.getMessage(),e);
			}
			log.info("finished scheduledQueries");
		} finally {
			partnerConnection.logout();
		}
	}
	/**
	 * Run the balanced forward processes. By default these run on the first of every month
	 * at 02:00 AM using Pacific/Auckland time.
	 */
	@Scheduled(
				cron="${com.harmoney.ims.core.server.web.Scheduler.balanceForward:0 0 2 * * *}",
				zone="${com.harmoney.ims.core.server.web.Scheduler.tz:Pacific/Auckland}"
				)
	@SchedulerLock(name = "scheduledBalanceForward")
	public void runBalanceForward() {
		log.info("starting scheduledBalanceForward");
		try {
			try {
				investorLoanTransactionBalanceForward.processBalanceForward(LocalDate.now());
			} catch (Exception e) {
				log.error(e.getMessage(),e);
			}
			try {
				investorFundTransactionBalanceForward.processBalanceForward(LocalDate.now());
			} catch (Exception e) {
				log.error(e.getMessage(),e);
			}
			log.info("finished scheduledBalanceForward");
		} finally {
			partnerConnection.logout();
		}
	}
}
