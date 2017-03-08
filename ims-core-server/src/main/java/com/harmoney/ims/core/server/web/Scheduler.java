/**
 * 
 */
package com.harmoney.ims.core.server.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.harmoney.ims.core.queries.AccountSummaryQuery;
import com.harmoney.ims.core.queries.InvestmentOrderQuery;
import com.sforce.ws.ConnectionException;

/**
 * @author Roger Parkinson
 *
 */
@Component
public class Scheduler {

    private static final Logger log = LoggerFactory.getLogger(Scheduler.class);

	@Autowired AccountSummaryQuery accountSummaryQuery;
	@Autowired InvestmentOrderQuery investmentOrderQuery;

	@Scheduled(fixedDelayString="${com.harmoney.ims.core.server.web.Scheduler:86400000}") // default is daily
	public void runQueries() {
		try {
			accountSummaryQuery.doQuery();
		} catch (ConnectionException e) {
			log.error(e.getMessage(),e);
		}
		try {
			investmentOrderQuery.doQuery();
		} catch (ConnectionException e) {
			log.error(e.getMessage(),e);
		}
	}
}
