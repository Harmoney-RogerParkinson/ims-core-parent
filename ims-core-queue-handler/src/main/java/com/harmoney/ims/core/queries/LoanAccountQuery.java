/**
 * 
 */
package com.harmoney.ims.core.queries;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.harmoney.ims.core.queueprocessor.AccountProcessor;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;

/**
 * @author Roger Parkinson
 *
 */
@Component
public class LoanAccountQuery {
	
	@Autowired private PartnerConnection partnerConnection;
	@Autowired private AccountProcessor accountSummaryProcessor;
	
	public int doQuery() throws ConnectionException {
		String queryString = "SELECT harMoney_Account_Number__c,Id,loan__Loan_Status__c,Name FROM loan__Loan_Account__c";

		QueryResult qr = partnerConnection.query(queryString);
		SObject[] records = qr.getRecords();
		accountSummaryProcessor.processQuery(records);
		return records.length;
	}

}
