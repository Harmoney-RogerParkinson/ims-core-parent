package com.harmoney.ims.core.server.scheduled;

import javax.sql.DataSource;

import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbc.JdbcLockProvider;
import net.javacrumbs.shedlock.spring.SpringLockableTaskSchedulerFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;

@Configuration
@EnableScheduling
@PropertySource("classpath:default.properties")
@ComponentScan({ "com.harmoney.ims.core.server.scheduled" })
public class SchedulerConfig extends WsConfigurerAdapter {

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
