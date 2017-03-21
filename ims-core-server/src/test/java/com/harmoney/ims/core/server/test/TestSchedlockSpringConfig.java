package com.harmoney.ims.core.server.test;

import javax.sql.DataSource;

import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbc.JdbcLockProvider;
import net.javacrumbs.shedlock.spring.SpringLockableTaskSchedulerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@ComponentScan({ "com.harmoney.ims.core.server.test" })
public class TestSchedlockSpringConfig {


	@Autowired
	DataSource dataSource;

	// needed for @PropertySource
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertyConfigInProd() {
		PropertySourcesPlaceholderConfigurer ret = new PropertySourcesPlaceholderConfigurer();
		return ret;
	}
	@Bean
	public LockProvider lockProvider(DataSource dataSource) {
		return new JdbcLockProvider(dataSource);
	}

	@Bean
	public TaskScheduler taskScheduler(LockProvider lockProvider) {
		int poolSize = 10;
		return SpringLockableTaskSchedulerFactory.newLockableTaskScheduler(
				poolSize, lockProvider);
	}
}
