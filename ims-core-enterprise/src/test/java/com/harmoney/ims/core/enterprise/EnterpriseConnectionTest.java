package com.harmoney.ims.core.enterprise;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sforce.soap.enterprise.EnterpriseConnection;
import com.sforce.soap.enterprise.QueryResult;
import com.sforce.soap.enterprise.sobject.Loan__Investor_Loan_Account_Txns__c;
import com.sforce.soap.enterprise.sobject.SObject;
import com.sforce.ws.ConnectionException;

@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource("/test.properties")
@ContextConfiguration(classes={EnterpriseConnectionSpringConfig.class})
public class EnterpriseConnectionTest {
	
    private static final Logger log = LoggerFactory.getLogger(EnterpriseConnectionTest.class);
	@Autowired private EnterpriseConnection enterpriseConnection;


	@Test
	public void test() throws ConnectionException {
		QueryResult qr = enterpriseConnection.query("SELECT Id,test__c FROM loan__Investor_Loan_Account_Txns__c where Management_Fee_Realised__c > 0");
		SObject[] records = qr.getRecords();
		for (SObject r: records) {
			Loan__Investor_Loan_Account_Txns__c ILT = (Loan__Investor_Loan_Account_Txns__c)r;
			log.info("Id={} Management_Fee_Realised__c={}",ILT.getId(),ILT..get);
		}

	}

}
