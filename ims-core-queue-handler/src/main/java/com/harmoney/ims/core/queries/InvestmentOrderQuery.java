/**
 * 
 */
package com.harmoney.ims.core.queries;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.harmoney.ims.core.partner.PartnerConnectionWrapper;
import com.harmoney.ims.core.queueprocessor.InvestmentOrderProcessor;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;

/**
 * @author Roger Parkinson
 *
 */
@Component
public class InvestmentOrderQuery {
	
	@Autowired private PartnerConnectionWrapper partnerConnection;
	@Autowired private InvestmentOrderProcessor investmentOrderProcessor;
	
	public static String SOQL = "SELECT Id                                       " +
            "      ,Name                                     " +
            "      ,CreatedDate                              " +
            "      ,loan__Account__c                         " +
            "      ,loan__Loan__c                            " +     //# Fkey to loan__Loan_Account__c (CL Contract)
            "      ,loan__Investment_Amount__c               " +
            "      ,HM_Investment_Amount__c                  " +
            "      ,Protect_Investment_Amount__c             " +
            "      ,loan__Charged_Off_Principal__c           " +
            "      ,loan__Charged_Off_Date__c                " +
            "      ,loan__Loan_Status__c                     " +
            "      ,loan__Share__c	                         " +
            "      ,Payment_Protect_Management_Fees__c       " +
            "      ,Payment_Protect_Sales_Commission_Fees__c " +
            "      ,Payment_Protect_Fee__c                   " +
            "      ,Payment_Protect_Rebated_Amount__c        " +
            "      ,HM_Rollup_Outstanding_Principal__c       " +
            "FROM   loan__Investor_Loan__c ";

	public static String SOQL2 = "SELECT Id,loan__Account__c,loan__Share__c,loan__Loan_Status__c FROM   loan__Investor_Loan__c ";
	
	public int doQuery() throws ConnectionException {

		SObject[] records = partnerConnection.query(SOQL);
		investmentOrderProcessor.processQuery(records);
		return records.length;
	}

	public static String getByLoanAccount(String loanAccountId) {
		return SOQL2 + "WHERE loan__Account__c = '"+loanAccountId+"'";
	}

}
