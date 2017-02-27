package com.harmoney.ims.core.queuehandler;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@ComponentScan("com.harmoney.ims.core.queuehandler.unpacker")
@PropertySource(value = { "classpath:test.properties" }, ignoreResourceNotFound = true)
public class UnpackerSpringConfig {

	// needed for @PropertySource
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertyConfigInProd() {
		PropertySourcesPlaceholderConfigurer ret = new PropertySourcesPlaceholderConfigurer();
		return ret;
	}
}
