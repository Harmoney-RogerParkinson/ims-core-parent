/**
 * 
 */
package com.harmoney.ims.core.queries;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.harmoney.ims.core.queueprocessor.BillProcessor;
import com.harmoney.ims.core.queueprocessor.PartnerConnectionWrapper;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;

/**
 * @author Roger Parkinson
 *
 */
@Component
public class BillQuery {
	
	@Autowired private PartnerConnectionWrapper partnerConnection;
	@Autowired private BillProcessor processor;
	
	public int doQuery() throws ConnectionException {
		String queryString = "SELECT  Id,Name,loan__Loan_Account__c, loan__Transaction_Date__c, "
				+ "CreatedDate, loan__Due_Date__c, loan__Due_Amt__c,loan__Payment_Satisfied__c, "
				+ "loan__waiver_applied__c "
				+ "FROM loan__Loan_account_Due_Details__c";

		SObject[] records = partnerConnection.query(queryString);
		processor.processQuery(records);
		return records.length;
	}

}