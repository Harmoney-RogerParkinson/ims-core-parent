package com.harmoney.ims.core.queuehandler;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.harmoney.ims.core.queuehandler.unpacker.Unpacker;

public class UnpackerTest {

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

}
