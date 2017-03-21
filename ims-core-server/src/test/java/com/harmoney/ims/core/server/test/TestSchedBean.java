package com.harmoney.ims.core.server.test;

import net.javacrumbs.shedlock.core.SchedulerLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TestSchedBean {

	@Value("${com.harmoney.ims.core.server.test.TestSchedBean:whatever}")
	private String id;

	private static final Logger log = LoggerFactory.getLogger(TestSchedBean.class);
	/**
	 * Run every 10 seconds, sleep for 10 seconds
	 */
	@Scheduled(
			cron="${com.harmoney.ims.core.server.web.Scheduler.queries:*/4 * * * * *}",
			zone="${com.harmoney.ims.core.server.web.Scheduler.tz:Pacific/Auckland}"
			)
	@SchedulerLock(name = "scheduledQueries")
	public void runQueries() {
		log.info("starting scheduledQueries {}",id);
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.info("finished scheduledQueries {}",id);
	}
}
