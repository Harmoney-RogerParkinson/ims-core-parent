/**
 * 
 */
package com.harmoney.ims.core.partner;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Roger Parkinson
 *
 */
@Component
public class ConfiguredSalesforceParameters {

	private static final Logger log = LoggerFactory.getLogger(ConfiguredSalesforceParameters.class);

	@Value("${salesforce.url}")
	private String salesforceURL;
	@Value("${salesforce.authEndpoint}")
	private String authEndpoint;
	@Value("${salesforce.username}")
	private String username;
	@Value("${salesforce.password}")
	private String password;
	@Value("${salesforce.security.token}")
	private String securityToken;
	@Value("${salesforce.timeout:5}")
	public long timeout;
    @Value("${salesforce.replayFrom:-1}")
	public long replayFrom;

	public String getSalesforceURL() {
		return salesforceURL;
	}


	public String getAuthEndpoint() {
		return authEndpoint;
	}


	public String getUsername() {
		return username;
	}


	public String getPassword() {
		return password;
	}


	public String getSecurityToken() {
		return securityToken;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(" salesforceURL: ");
		sb.append(salesforceURL);
		sb.append(" authEndpoint: ");
		sb.append(authEndpoint);
		sb.append(" username: ");
		sb.append(username);
		return sb.toString();
	}


	public long getTimeout() {
		return timeout;
	}


	public long getReplayFrom() {
		return replayFrom;
	}

	@PostConstruct
	public void init() {
		log.info("Salesforce configuration: {}",this);
	}


}
