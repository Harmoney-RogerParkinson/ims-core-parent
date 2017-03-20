/**
 * 
 */
package com.harmoney.ims.core.database;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @author Roger Parkinson
 *
 */
@Component
public class ConfiguredDatabaseParameters {
	
	private static final Logger log = LoggerFactory.getLogger(ConfiguredDatabaseParameters.class);

	@Value("${database.dialect:org.hibernate.dialect.PostgreSQLDialect}")
	private String dialect;
	@Value("${database.datasource.class:org.postgresql.Driver}")
	private String datasourceClass;
	@Value("${database.url:jdbc:postgresql:imscore}")
	private String datasourceURL;
	@Value("${database.user:postgres}")
	private String user;
	@Value("${database.password:postgres}")
	private String password;
	@Value("${database.hbm2ddl.auto:}")
	private String hbm2ddlAuto;

	public String getDialect() {
		return dialect;
	}
	public String getDatasourceClass() {
		return datasourceClass;
	}
	public String getDatasourceURL() {
		return datasourceURL;
	}
	public String getUser() {
		return user;
	}
	public String getPassword() {
		return password;
	}
	public String getHbm2ddlAuto() {
		return hbm2ddlAuto;
	}
	public boolean isHbm2ddlautoCreate() {
		return (StringUtils.hasText(hbm2ddlAuto) && hbm2ddlAuto.startsWith("create"));
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("\ndialect: ");
		sb.append(dialect);
		sb.append("\ndatasourceClass: ");
		sb.append(datasourceClass);
		sb.append("\ndatasourceURL: ");
		sb.append(datasourceURL);
		sb.append("\nhbm2ddlAuto: ");
		sb.append(hbm2ddlAuto);
		return sb.toString();
	}
	@PostConstruct
	public void init() {
		log.info("Database configuration: {}",this);
	}
}
