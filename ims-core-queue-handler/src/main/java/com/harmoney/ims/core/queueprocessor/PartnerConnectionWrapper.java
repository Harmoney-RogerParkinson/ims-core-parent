package com.harmoney.ims.core.queueprocessor;

import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;

public interface PartnerConnectionWrapper {

	public abstract SObject[] query(String queryString)
			throws ConnectionException;

}