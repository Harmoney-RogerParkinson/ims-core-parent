package com.harmoney.ims.core.queuehandler;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.harmoney.ims.core.instances.InvestorLoanTransaction;
import com.harmoney.ims.core.queuehandler.unpacker.Result;
import com.harmoney.ims.core.queuehandler.unpacker.Unpacker;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={UnpackerSpringConfig.class})
public class UnpackerTest {
	
    private static final Logger log = LoggerFactory.getLogger(UnpackerTest.class);
	
	@Autowired Unpacker unpacker;

	@Test
	public void testFixVariableFormat() {
		Assert.assertEquals("managementFeeRealised", Unpacker.fixVariableFormat("Management_Fee_Realised__c"));
		Assert.assertEquals("createdDate", Unpacker.fixVariableFormat("CreatedDate"));
		Assert.assertEquals("principalPaid", Unpacker.fixVariableFormat("loan__Principal_Paid__c"));
		Assert.assertEquals("investorLoanTransactions", Unpacker.fixVariableFormat("InvestorLoanTransactions"));
	}
//	@Test
//	public void dates() throws ParseException {
//		String sampleDate = "2017-02-23T00:00:00.000Z";
//		
//		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
//		ZonedDateTime zdt = ZonedDateTime.parse(sampleDate);
//		LocalDateTime ldt = zdt.toLocalDateTime();
//		Timestamp result = java.sql.Timestamp.valueOf(ldt);
//		OffsetDateTime odt = OffsetDateTime.parse(sampleDate);
//		odt.toString();
//	}
	
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
		sobject.put("test__c", "RJP13:18:26.807");
		sobject.put("loan__Investor_Loan__c", "a4Jp00000009JRdEAM");
		sobject.put("loan__Late_Fees_Paid__c", new Double(0));
		sobject.put("loan__Charged_Off_Fees__c", new Double(0));
		sobject.put("loan__Total_Service_Charge__c", new Double(0));
		sobject.put("loan__Waived__c", new Boolean(false));
		sobject.put("Name", "ILTID-0002902106");
		sobject.put("loan__Charged_Off_Interest__c", new Double(0));
		sobject.put("Investor_Txn_Fee__c", new Double(0));
		sobject.put("loan__Principal_Paid__c", new Double(0));
		sobject.put("loan__Tax__c", new Double(0));
		sobject.put("CreatedDate", "2016-05-17T03:51:23.000Z");
		sobject.put("loan__Rebate_Amount_On_Payoff__c", new Double(0));
		sobject.put("Id", "a4Ip00000004IhIEAU");
		sobject.put("loan__Charged_Off_Date__c", "2015-04-26T00:00:00.000Z");
		sobject.put("loan__Protect_Principal__c", null);
		sobject.put("loan__Txn_Code__c", "CHARGE OFF");
		sobject.put("loan__Txn_Type__c", null);
		Map<String,Map<String,Object>> ret = new HashMap<>();
		ret.put("sobject", sobject);
		return ret;
	}
	
	@Test
	public void unpack() {
		InvestorLoanTransaction target = new InvestorLoanTransaction();
		Result result = unpacker.unpack(getMap(), target);
		log.debug("{}",result);
		Assert.assertEquals(4, result.getErrors().size());
		Assert.assertEquals(2, result.getWarnings().size());
	}
	
	

}
