package com.harmoney.ims.core.queueprocessor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;

@Component
@Profile("queue-handler-prod")
public class PartnerConnectionWrapperImpl implements PartnerConnectionWrapper {
	
	@Autowired private PartnerConnection partnerConnection;

	public PartnerConnectionWrapperImpl() {
		
	}
	
	/* (non-Javadoc)
	 * @see com.harmoney.ims.core.queries.PartnerConnectionWrapper#query(java.lang.String)
	 */
	@Override
	public SObject[] query(String queryString) throws ConnectionException {
		QueryResult qr = partnerConnection.query(queryString);
		return qr.getRecords();
	}

}
