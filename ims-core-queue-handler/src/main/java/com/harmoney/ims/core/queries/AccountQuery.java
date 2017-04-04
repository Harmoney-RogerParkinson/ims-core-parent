/**
 * 
 */
package com.harmoney.ims.core.queries;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.harmoney.ims.core.partner.PartnerConnectionWrapper;
import com.harmoney.ims.core.queueprocessor.AccountProcessor;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;

/**
 * @author Roger Parkinson
 *
 */
@Component
public class AccountQuery {
	
	@Autowired private PartnerConnectionWrapper partnerConnection;
	@Autowired private AccountProcessor accountSummaryProcessor;
	
	public int doQuery() throws ConnectionException {
		String queryString = "Select Id                                 " +
                "      ,harMoney_Account_Number__c         " +
                "      ,Name                               " +
                "      ,Outstanding_Principal__c           " +
                "      ,loan__Deployed_Funds__c            " +
                "      ,loan__Undeployed_Funds__c          " +
                "      ,Total_Paid_Off_Amount__c           " +
                "      ,Interests_Recived__c               " +
                "      ,Late_Fees__c                       " +
                "      ,Service_Fees__c                    " +
                "      ,Total_Tax__c                       " +
                "      ,Total_Charged_Off_Principal__c     " +
                "      ,Total_Deposit__c                   " +
                "      ,Total_Withdrawal__c                " +
//                "      ,Full_investor_statement__c         " +
                "      ,loan__Investor_Tax_Configuration__r.loan__Tax_Percentage__c " +
                "      ,IRD_Number__c                                               " +
                "      ,Account_Summary__c                 " +
                "From   Account                       ";

		SObject[] records = partnerConnection.query(queryString);
		accountSummaryProcessor.processQuery(records);
		return records.length;
	}

}
