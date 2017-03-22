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
		String queryString = "Select Id                                       " +
                "      ,Name                                     " +
                "      ,loan__Loan_Account__c                    " + // Loan Account FKey (CL Contract or loan__Loan_Account__c)
                "      ,loan__Transaction_Amount__c              " +
                "      ,Protect_Realised__c                      " +
                // More fields to add here
                "From   loan__Repayment_Schedule__c";

		QueryResult qr = partnerConnection.query(queryString);
		SObject[] records = qr.getRecords();
		amortizationScheduleProcessor.processQuery(records);
		return records.length;
	}
}