/**
 * 
 */
package com.harmoney.ims.core.queries;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.harmoney.ims.core.queueprocessor.AmortizationScheduleProcessor;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;

/**
 * @author Roger Parkinson
 *
 */
@Component
public class AmortizationScheduleQuery {
	
	@Autowired private PartnerConnection partnerConnection;
	@Autowired private AmortizationScheduleProcessor amortizationScheduleProcessor;
	
	public int doQuery() throws ConnectionException {
		String queryString = "SELECT Id,Name, loan__Loan_Account__c,loan__Due_Date__c, Protect_Realised__c, "
				+ "Sales_Commission_Realised__c, Management_Fee_Realised__c "
				+ "FROM loan__Repayment_Schedule__c";

		QueryResult qr = partnerConnection.query(queryString);
		SObject[] records = qr.getRecords();
		amortizationScheduleProcessor.processQuery(records);
		return records.length;
	}
}