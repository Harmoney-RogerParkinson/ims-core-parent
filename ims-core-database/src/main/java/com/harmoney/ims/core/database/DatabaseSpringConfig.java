package com.harmoney.ims.core.database;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * This is a Spring Configuration class that defines the beans needed for
 * the database. 
 * 
 * @author Roger Parkinson
 *
 */
@Configuration
@EnableTransactionManagement
@ComponentScan("com.harmoney.ims.core.database")
public class DatabaseSpringConfig {
	
	@Autowired ConfiguredDatabaseParameters configuredParameters;

	// needed for @PropertySource
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
		return new PropertySourcesPlaceholderConfigurer();
	}
	  @Bean
	  public DataSource dataSource() {

	    DriverManagerDataSource driver = new DriverManagerDataSource();
	    driver.setDriverClassName(configuredParameters.getDatasourceClass());
	    driver.setUrl(configuredParameters.getDatasourceURL());
	    driver.setUsername(configuredParameters.getUser());
	    driver.setPassword(configuredParameters.getPassword());
	    return driver;	  
	  }

	  @Bean
	  public LocalContainerEntityManagerFactoryBean entityManagerFactory() {

	    HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
	    vendorAdapter.setGenerateDdl(true);
	    vendorAdapter.setDatabasePlatform(configuredParameters.getDialect());

	    LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
	    factory.setJpaVendorAdapter(vendorAdapter);
	    factory.setDataSource(dataSource());
//	    factory.setPackagesToScan("com.harmoney.ims.core.instances");
	    Properties jpaProperties = new Properties();
//	    jpaProperties.put("hibernate.transaction.jta.platform", "nz.co.senanque.hibernate.SpringJtaPlatformAdapter");
	    jpaProperties.put("hibernate.dialect", configuredParameters.getDialect());
	    jpaProperties.put("hibernate.format_sql", true);
	    jpaProperties.put("hibernate.connection.autocommit", false);
	    jpaProperties.put("hibernate.hbm2ddl.auto", configuredParameters.getHbm2ddlAuto());
	    factory.setJpaProperties(jpaProperties);
	    return factory;
	  }

	  @Bean
	  public PlatformTransactionManager transactionManager() {

	    JpaTransactionManager txManager = new JpaTransactionManager();
	    txManager.setEntityManagerFactory(entityManagerFactory().getObject());
	    return txManager;
	  }
}
