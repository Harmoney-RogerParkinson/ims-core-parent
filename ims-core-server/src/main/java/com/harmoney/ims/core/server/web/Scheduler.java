/**
 * 
 */
package com.harmoney.ims.core.server.web;

import java.time.LocalDate;

import net.javacrumbs.shedlock.core.SchedulerLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.harmoney.ims.core.balanceforward.InvestorLoanTransactionBalanceForward;
import com.harmoney.ims.core.queries.AccountQuery;
import com.harmoney.ims.core.queries.InvestmentOrderQuery;
import com.sforce.ws.ConnectionException;

/**
 * Schedules various background processes to run.
 * Note that all servers will run these processes at the scheduled time.
 * 
 * @author Roger Parkinson
 *
 */
@Component
public class Scheduler {

    private static final Logger log = LoggerFactory.getLogger(Scheduler.class);

	@Autowired AccountQuery accountQuery;
	@Autowired InvestmentOrderQuery investmentOrderQuery;
	@Autowired InvestorLoanTransactionBalanceForward investorLoanTransactionBalanceForward;

	@Scheduled(fixedDelayString="${com.harmoney.ims.core.server.web.Scheduler.queries:86400000}") // default is daily
	@SchedulerLock(name = "scheduledQueries")
	public void runQueries() {
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
	}
	/**
	 * Run the balanced forward processes. By default these run on the first of every month
	 * at 01:00 AM using Pacific/Auckland time.
	 */
	@Scheduled(
				cron="${com.harmoney.ims.core.server.web.Scheduler.balanceForward:0 0 1 1 1/1 ? *}",
				zone="${com.harmoney.ims.core.server.web.Scheduler.balanceForwardtz:Pacific/Auckland}"
				)
	@SchedulerLock(name = "scheduledBalanceForward")
	public void runBalanceForward() {
		try {
			investorLoanTransactionBalanceForward.processBalanceForward(LocalDate.now());
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
	}
}
