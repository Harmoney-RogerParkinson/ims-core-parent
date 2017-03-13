package com.harmoney.ims.core.server.web;

import javax.sql.DataSource;

import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbc.JdbcLockProvider;
import net.javacrumbs.shedlock.spring.SpringLockableTaskSchedulerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import com.harmoney.ims.core.messages.MessageProcessorSpringConfig;
import com.harmoney.ims.core.queuehandler.QueueHandlerSpringConfig;

@Configuration
@EnableWebMvc
@EnableScheduling
@ComponentScan({ "com.harmoney.ims.core.server.web" })
@Import({ MessageProcessorSpringConfig.class, QueueHandlerSpringConfig.class })
@Profile("prod")
public class WebConfig extends WebMvcConfigurerAdapter {

	@Autowired
	DataSource dataSource;

	@Bean
	public ViewResolver viewResolver() {
		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
		viewResolver.setViewClass(JstlView.class);
		viewResolver.setPrefix("/WEB-INF/views/");
		viewResolver.setSuffix(".jsp");
		return viewResolver;
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
