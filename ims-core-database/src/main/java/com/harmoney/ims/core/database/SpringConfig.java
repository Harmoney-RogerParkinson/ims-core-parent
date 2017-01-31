package com.harmoney.ims.core.database;

import java.util.Properties;

import javax.sql.DataSource;

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
public class SpringConfig {
	
	@Value("${database.dialect:org.hibernate.dialect.PostgreSQLDialect}")
	public String dialect;
	@Value("${database.datasource.class:org.postgresql.Driver}")
	public String datasourceClass;
	@Value("${database.url:jdbc:postgresql:localhost:imscore}")
	public String datasourceURL;
	@Value("${database.user:postgres}")
	public String user;
	@Value("${database.password:postgres}")
	public String password;

	// needed for @PropertySource
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
		return new PropertySourcesPlaceholderConfigurer();
	}
	  @Bean
	  public DataSource dataSource() {

	    DriverManagerDataSource driver = new DriverManagerDataSource();
	    driver.setDriverClassName(datasourceClass);
	    driver.setUrl("jdbc:postgresql:imscore");
	    driver.setUsername(user);
	    driver.setPassword(password);
	    return driver;	  
	  }

	  @Bean
	  public LocalContainerEntityManagerFactoryBean entityManagerFactory() {

	    HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
	    vendorAdapter.setGenerateDdl(true);
	    vendorAdapter.setDatabasePlatform(dialect);

	    LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
	    factory.setJpaVendorAdapter(vendorAdapter);
	    factory.setDataSource(dataSource());
//	    factory.setPackagesToScan("com.harmoney.ims.core.instances");
	    Properties jpaProperties = new Properties();
//	    jpaProperties.put("hibernate.transaction.jta.platform", "nz.co.senanque.hibernate.SpringJtaPlatformAdapter");
	    jpaProperties.put("hibernate.dialect", dialect);
	    jpaProperties.put("hibernate.format_sql", true);
	    jpaProperties.put("hibernate.connection.autocommit", false);
	    factory.setJpaProperties(jpaProperties);
	    return factory;
//	    factory.afterPropertiesSet();
//	    return factory.getObject();
	  }

	  @Bean
	  public PlatformTransactionManager transactionManager() {

	    JpaTransactionManager txManager = new JpaTransactionManager();
	    txManager.setEntityManagerFactory(entityManagerFactory().getObject());
	    return txManager;
	  }
}
