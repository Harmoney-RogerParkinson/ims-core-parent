/**
 * 
 */
package com.harmoney.ims.core.queries;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.harmoney.ims.core.partner.PartnerConnectionWrapper;
import com.harmoney.ims.core.queueprocessor.LoanAccountProcessor;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;

/**
 * @author Roger Parkinson
 *
 */
@Component
public class LoanAccountQuery {
	
	@Autowired private PartnerConnectionWrapper partnerConnection;
	@Autowired private LoanAccountProcessor processor;
	
	public static String SOQL = "SELECT harMoney_Account_Number__c,Id,loan__Loan_Status__c,Name,Waived__c FROM loan__Loan_Account__c ";
	
	
	public int doQuery() throws ConnectionException {

		SObject[] records = partnerConnection.query(SOQL);
		processor.processQuery(records);
		return records.length;
	}

}
