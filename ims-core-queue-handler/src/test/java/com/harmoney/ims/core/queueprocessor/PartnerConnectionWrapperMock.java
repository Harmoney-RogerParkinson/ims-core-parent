package com.harmoney.ims.core.queueprocessor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;

@Component
@Profile("queue-handler-dev")
public class PartnerConnectionWrapperMock implements PartnerConnectionWrapper {
	
	@Autowired private PartnerConnection partnerConnection;

	public PartnerConnectionWrapperMock() {
		
	}
	
	/* (non-Javadoc)
	 * @see com.harmoney.ims.core.queries.PartnerConnectionWrapper#query(java.lang.String)
	 */
	@Override
	public SObject[] query(String queryString) throws ConnectionException {
		SObject mock = new SObject();
		mock.setId("xyz");
		mock.setField("a", "b");
		return new SObject[]{mock};
	}

}
