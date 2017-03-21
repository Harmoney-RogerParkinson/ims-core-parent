package com.harmoney.ims.core.database.descriptors;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harmoney.ims.core.instances.InvestorLoanTransaction;

public class ObjectDescriptorTest {
	
    private static final Logger log = LoggerFactory.getLogger(ObjectDescriptorTest.class);
	
//	@Autowired Unpacker unpacker;

	@Test
	public void testFixVariableFormat() {
		Assert.assertEquals("managementFeeRealised", ObjectDescriptor.fixVariableFormat("Management_Fee_Realised__c"));
		Assert.assertEquals("createdDate", ObjectDescriptor.fixVariableFormat("CreatedDate"));
		Assert.assertEquals("principalPaid", ObjectDescriptor.fixVariableFormat("loan__Principal_Paid__c"));
		Assert.assertEquals("investorLoanTransactions", ObjectDescriptor.fixVariableFormat("InvestorLoanTransactions"));
	}
	@Test
	public void dates() throws ParseException {
		
		
		LocalDateTime now = LocalDateTime.now();
		System.out.println("Pre-Truncate:  " + now);
		DateTimeFormatter dtf = DateTimeFormatter.ISO_DATE_TIME;
		System.out.println("Post-Truncate: " + now.truncatedTo(ChronoUnit.MICROS).format(dtf));
		System.out.println("Post-Truncate: " + now.truncatedTo(ChronoUnit.MILLIS).format(dtf));
		System.out.println("Post-Truncate: " + now.truncatedTo(ChronoUnit.NANOS).format(dtf));
		
//		String sampleDate = "2017-02-23T00:00:00.000Z";
//		
//		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
//		ZonedDateTime zdt = ZonedDateTime.parse(sampleDate);
//		LocalDateTime ldt = zdt.toLocalDateTime();
//		Timestamp result = java.sql.Timestamp.valueOf(ldt);
//		OffsetDateTime odt = OffsetDateTime.parse(sampleDate);
//		odt.toString();
	}
	
//	@Test
//	public void testEnum() {
//		TxnCode result = TxnCode.valueOf("CHARGE_OFF");
//		result.toString();
//	}
	
	private Map<String,Map<String,Object>> getMap() {
/*
{loan__Charged_Off_Principal__c=311.56, 
IsDeleted=false, 
loan__Interest_Paid__c=0.0, 
test__c=RJP13:18:26.807, 
loan__Investor_Loan__c=a4Jp00000009JRdEAM, 
loan__Late_Fees_Paid__c=0.0, 
loan__Charged_Off_Fees__c=0.0, 
loan__Total_Service_Charge__c=0.0, 
loan__Waived__c=false, 
Name=ILTID-0002902106, 
Investor_Txn_Fee__c=0.0, 
loan__Charged_Off_Interest__c=0.0, 
loan__Principal_Paid__c=0.0, 
loan__Tax__c=0.0, 
CreatedDate=2016-05-17T03:51:23.000Z, 
loan__Rebate_Amount_On_Payoff__c=0.0, 
Id=a4Ip00000004IhIEAU, 
loan__Charged_Off_Date__c=2015-04-26T00:00:00.000Z, 
loan__Protect_Principal__c=null, 
loan__Txn_Code__c=CHARGE OFF}
*/
		Map<String,Object> sobject = new HashMap<>();
		sobject.put("loan__Charged_Off_Principal__c", new Double(311.56));
		sobject.put("IsDeleted", new Boolean(false));
		sobject.put("loan__Interest_Paid__c", new Double(0));
		sobject.put("loan__Account__c", "whatever");
		sobject.put("test__c", "RJP13:18:26.807");
		sobject.put("loan__Investor_Loan__c", "a4Jp00000009JRdEAM");
		sobject.put("Investment_Order_Status__c", "Investment_Order_Status__c");
		sobject.put("loan__Late_Fees_Paid__c", new Double(0));
		sobject.put("loan__Charged_Off_Fees__c", new Double(0));
		sobject.put("loan__Total_Service_Charge__c", new Double(0));
		sobject.put("loan__Waived__c", new Boolean(false));
		sobject.put("Name", "ILTID-0002902106");
		sobject.put("loan__Charged_Off_Interest__c", new Double(0));
		sobject.put("Investor_Txn_Fee__c", new Double(0));
		sobject.put("loan__Principal_Paid__c", new Double(0));
		sobject.put("Protect_Realised__c", new Double(0));
		sobject.put("Management_Fee_Realised__c", new Double(0));
		sobject.put("Sales_Commission_Fee_Realised__c", new Double(0));
		sobject.put("Net_Amount__c", new Double(0));
		sobject.put("loan__Tax__c", new Double(0));
		sobject.put("CreatedDate", "2016-05-17T03:51:23.000Z");
		sobject.put("loan__Rebate_Amount_On_Payoff__c", new Double(0));
		sobject.put("Id", "a4Ip00000004IhIEAU");
		sobject.put("loan__Charged_Off_Date__c", "2015-04-26T00:00:00.000Z");
		sobject.put("loan__Protect_Principal__c", null);
		sobject.put("loan__Txn_Code__c", "CHARGE OFF");
		sobject.put("loan__Txn_Type__c", null);
		sobject.put("Rejected__c", new Boolean(false));
		sobject.put("Reversed__c", new Boolean(false));
		sobject.put("Reverse_Rejected_Date__c", null);
		sobject.put("Account_ID__c", "1111");
		Map<String,Map<String,Object>> ret = new HashMap<>();
		ret.put("sobject", sobject);
		return ret;
	}
	
	@Test
	public void unpack() {
		ObjectDescriptorGenerator objectDescriptorGenerator = new ObjectDescriptorGenerator();
		InvestorLoanTransaction target = new InvestorLoanTransaction();
		ObjectDescriptor objectDescriptor = objectDescriptorGenerator.build(InvestorLoanTransaction.class);
		Result result = objectDescriptor.unpack(getMap().get("sobject"), target);
		log.debug("{} [{}]",result,TimeZone.getDefault());
		Assert.assertEquals(0, result.getErrors().size());
		Assert.assertEquals(0, result.getWarnings().size());
		TimeZone tz = TimeZone.getDefault();
		tz.toString();
		
	}
	
	

}
