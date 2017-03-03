package com.harmoney.ims.core.messages;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FieldResolverILT implements FieldResolver {

	private static final Logger log = LoggerFactory
			.getLogger(FieldResolverILT.class);
	@Override
	public void resolve(Map<String, Object> sobject) {
		log.debug("");
		
		String Loan_Payment_Transaction_Remark__c = (String)sobject.get("Loan_Payment_Transaction_Remark__c");
		boolean rewrite = (Loan_Payment_Transaction_Remark__c == null)?false:Loan_Payment_Transaction_Remark__c.indexOf("Rewrite")>-1;
		Double Original_Protect_Realised__c = (Double)sobject.get("Original_Protect_Realised__c");
		Double Loan_Payment_Transaction_Protect_Realised__c = (Double)sobject.get("Loan_Payment_Transaction_Protect_Realise__c");
		Double Share_Rounded__c = (Double)sobject.get("Share_Rounded__c");
		Double Extra_Protect_Realised__c = (Double)sobject.get("Extra_Protect_Realised__c");
		Double HP_Management_Fee__c = (Double)sobject.get("HP_Management_Fee__c");
		Double Protect_Fee_Amount__c = (Double)sobject.get("Protect_Fee_Amount__c");
		Double Protect_Realised_Active__c = (Double)sobject.get("Protect_Realised_Active__c");
		Double Protect_Charge_Off__c = (Double)sobject.get("Protect_Charge_Off__c");
		Double HP_Sale_Commission_Fee__c = (Double)sobject.get("HP_Sale_Commission_Fee__c");
		Boolean Protect_Enabled__c = (Boolean)sobject.get("Protect_Enabled__c");
		boolean chargeOff = "CHARGE OFF".equals((String)sobject.get("loan__Txn_Code__c"));

		Double Protect_Realised__c = new Double(0);
		if (Protect_Enabled__c) {
			if (chargeOff) {
				Protect_Realised__c = 0D;
			} else {
				Protect_Realised__c = Loan_Payment_Transaction_Protect_Realised__c
						* Share_Rounded__c;
			}
		} else {
			Protect_Realised__c = 0D;
		}
		sobject.put("Protect_Realised__c", Protect_Realised__c);

		Double Management_Fee_Realised__c = new Double(0);
		if ("Full waiver".equals(Loan_Payment_Transaction_Remark__c)
				|| chargeOff) {
			Management_Fee_Realised__c = Original_Protect_Realised__c
					* Share_Rounded__c;
		} else {
			if ("Early repayment - with waiver"
					.equals(Loan_Payment_Transaction_Remark__c) || rewrite) {
				Management_Fee_Realised__c = (Original_Protect_Realised__c + Protect_Realised_Active__c
						* Share_Rounded__c);
			} else {
				Management_Fee_Realised__c = Protect_Realised__c;
			}
		}
		if (Protect_Fee_Amount__c > 0) {
			Management_Fee_Realised__c = Management_Fee_Realised__c
					* HP_Management_Fee__c / Protect_Fee_Amount__c;
		}
		sobject.put("Management_Fee_Realised__c", Management_Fee_Realised__c);

		Double Sales_Commission_Fee_Realised__c = new Double(0);
		if ("Early repayment - with waiver"
				.equals(Loan_Payment_Transaction_Remark__c)) {
			Sales_Commission_Fee_Realised__c = (Protect_Fee_Amount__c
					- Protect_Realised_Active__c + Original_Protect_Realised__c)
					* Share_Rounded__c;
		} else if (rewrite) {
			Sales_Commission_Fee_Realised__c = (Original_Protect_Realised__c + Extra_Protect_Realised__c)
					* Share_Rounded__c;
		} else if (chargeOff) {
			Sales_Commission_Fee_Realised__c = Protect_Charge_Off__c
					* Share_Rounded__c;
		} else {
			Sales_Commission_Fee_Realised__c = Protect_Realised__c;
		}
		if (Protect_Fee_Amount__c > 0) {
			Sales_Commission_Fee_Realised__c = Sales_Commission_Fee_Realised__c
					* HP_Sale_Commission_Fee__c / Protect_Fee_Amount__c;
		}
		sobject.put("Sales_Commission_Fee_Realised__c",
				Sales_Commission_Fee_Realised__c);
		
	}

}
