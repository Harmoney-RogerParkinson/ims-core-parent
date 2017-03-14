package com.harmoney.ims.core.queries;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@ComponentScan(value={"com.harmoney.ims.core.queueprocessor","com.harmoney.ims.core.queries","com.harmoney.ims.core.balanceforward"})
@PropertySource(value = { "classpath:test.properties" }, ignoreResourceNotFound = true)
public class QuerySpringConfig {


	// needed for @PropertySource
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertyConfigInProd() {
		PropertySourcesPlaceholderConfigurer ret = new PropertySourcesPlaceholderConfigurer();
		return ret;
	}
}
