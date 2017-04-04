package com.harmoney.ims.core.partner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

@Component
@Profile({"queue-handler-prod","message-processor-dev"})
public class PartnerConnectionWrapperImpl implements PartnerConnectionWrapper {
	
    private static final Logger log = LoggerFactory.getLogger(PartnerConnectionWrapperImpl.class);

	@Autowired ConfiguredSalesforceParameters configuredParameters;

	public PartnerConnectionWrapperImpl() {
		
	}
	
	/* (non-Javadoc)
	 * @see com.harmoney.ims.core.queries.PartnerConnectionWrapper#query(java.lang.String)
	 */
	@Override
	public SObject[] query(String queryString) throws ConnectionException {
		ConnectorConfig config = new ConnectorConfig();
		config.setUsername(configuredParameters.getUsername());
		config.setPassword(configuredParameters.getPassword()+configuredParameters.getSecurityToken());
		config.setAuthEndpoint(configuredParameters.getAuthEndpoint());
//		log.debug("Salesforce Partner Connection:\nAuth EndPoint: {}\nService EndPoint: {}\nUsername: {}\nSessionId: {}",
//				config.getAuthEndpoint(),config.getServiceEndpoint(),config.getUsername(),config.getSessionId());
		PartnerConnection partnerConnection = new PartnerConnection(config);
		QueryResult qr = partnerConnection.query(queryString);
		partnerConnection.logout();
		return qr.getRecords();
	}
	
	public void logout() {
	}
	

}
