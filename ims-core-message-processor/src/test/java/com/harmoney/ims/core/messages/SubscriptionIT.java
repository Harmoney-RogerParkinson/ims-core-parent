package com.harmoney.ims.core.messages;

import static org.junit.Assert.assertNotNull;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.salesforce.emp.connector.EmpConnector;
import com.sforce.soap.enterprise.EnterpriseConnection;
import com.sforce.soap.enterprise.Error;
import com.sforce.soap.enterprise.SaveResult;
import com.sforce.soap.enterprise.sobject.Invoice_Statement__c;
import com.sforce.ws.ConnectionException;

/**
 * This is an integration test. It waits on a topic and while it is waiting
 * sends a SOAP create to Salesforce so that a message will be sent to the topic.
 * The message will be processed/logged on another thread by the message handler.
 * Because the active profile is 'dev' the mock message handler will be injected rather
 * than the production 'prod' one. The mock message handler counts the message and interrupts
 * the main thread from its sleep.
 * 
 * It is assumed that the Invoice_Statement__c record in defined in Salesforce and that
 * the topic is listening for changes on Invoice_Statement__c.
 * 
 * @author Roger Parkinson
 * 
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={MessageProcessorSpringConfig.class})
@PropertySource("classpath:test.properties")
@ActiveProfiles("dev")
public class SubscriptionIT {
	
    private static final Logger log = LoggerFactory.getLogger(SubscriptionIT.class);
	@Autowired private EmpConnector empConnector;
	@Autowired private MessageHandlerMock messageHandler;
	
	@Autowired private EnterpriseConnection enterpriseConnection;

	@Test
	public void testSubscription() throws ConnectionException, InterruptedException {
		assertNotNull(empConnector);
		createInvoiceStatement();
		messageHandler.getLatch().await(10000, TimeUnit.MILLISECONDS);
	}

	private void createInvoiceStatement() throws ConnectionException {
		Invoice_Statement__c[] records = new Invoice_Statement__c[1];
		Invoice_Statement__c record = new Invoice_Statement__c();
		record.setDescription__c("whatever");
		record.setStatus__c("Open");
		records[0] = record;
		SaveResult[] saveResults = enterpriseConnection.create(records);
		// check the returned results for any errors
		for (int i = 0; i < saveResults.length; i++) {
			if (saveResults[i].isSuccess()) {
				log.debug(i
						+ ". Successfully created record - Id: "
						+ saveResults[i].getId());
			} else {
				Error[] errors = saveResults[i].getErrors();
				for (int j = 0; j < errors.length; j++) {
					log.debug("ERROR creating record: "
							+ errors[j].getMessage());
				}
			}
		}
		
	}
}
