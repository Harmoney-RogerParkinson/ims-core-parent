/**
 * 
 */
package com.harmoney.ims.core.partner;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sforce.soap.partner.PartnerConnection;
import com.sforce.ws.ConnectorConfig;

/**
 * @author Roger Parkinson
 *
 */
@Component
public class PartnerConnectionFactory implements FactoryBean<PartnerConnection> {
	
	@Autowired ConfiguredSalesforceParameters configuredParameters;


	public PartnerConnectionFactory() {
	}

	@Override
	public PartnerConnection getObject() throws Exception {
		ConnectorConfig config = new ConnectorConfig();
		config.setUsername(configuredParameters.getUsername());
		config.setPassword(configuredParameters.getPassword()+configuredParameters.getSecurityToken());
		config.setAuthEndpoint(configuredParameters.getAuthEndpoint());
		PartnerConnection partnerConnection = new PartnerConnection(config);
		return partnerConnection;
	}

	@Override
	public Class<?> getObjectType() {
		return PartnerConnection.class;
	}

	@Override
	public boolean isSingleton() {
		return false;
	}

}
