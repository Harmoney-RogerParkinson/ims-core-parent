/**
 * 
 */
package com.harmoney.ims.core.queries;

import java.util.Date;

import org.springframework.stereotype.Component;

import com.harmoney.ims.core.database.ConvertUtils;

/**
 * @author Roger Parkinson
 *
 */
@Component
public class AmortizationScheduleQuery {
	
	public static String SOQL = "SELECT Id,Name, loan__Loan_Account__c,loan__Due_Date__c, Protect_Realised__c, "
			+ "Sales_Commission_Realised__c, Management_Fee_Realised__c "
			+ "FROM loan__Repayment_Schedule__c ";
	
	public static String getByLoanAccount(String loanAccountId) {
		return SOQL + "WHERE loan__Loan_Account__c = '"+loanAccountId+"' order by loan__Due_Date__c";
	}

	public static String getByLoanAccountDueDate(String loanAccountId,
			Date dueDate) {
		return SOQL	+ "WHERE loan__Loan_Account__c = '"+loanAccountId+"' and loan__Due_Date__c = "+ConvertUtils.printDate(dueDate);
	}
}