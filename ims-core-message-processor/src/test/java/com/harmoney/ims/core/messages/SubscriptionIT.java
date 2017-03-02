package com.harmoney.ims.core.messages;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.harmoney.ims.core.partner.PartnerConnectionSpringConfig;
import com.salesforce.emp.connector.EmpConnector;
import com.sforce.soap.partner.Error;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.SaveResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;

/**
 * This is an integration test. It waits on a pushTopic and while it is waiting
 * sends a SOAP create to Salesforce so that a message will be sent to the pushTopic.
 * The message will be processed/logged on another thread by the message handler.
 * Because the active profile is 'dev' the mock message handler will be injected rather
 * than the production 'prod' one. The mock message handler counts the message and interrupts
 * the main thread from its sleep.
 * 
 * It is assumed that the Invoice_Statement__c record in defined in Salesforce and that
 * the topic is listening for changes on Invoice_Statement__c.
 * 
 * Uses the rjpsandbox sandbox.
 * 
 * @author Roger Parkinson
 * 
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource("/test.properties")
@ContextConfiguration(classes={MessageProcessorSpringConfig.class,PartnerConnectionSpringConfig.class})
@ActiveProfiles("message-processor-dev")
public class SubscriptionIT {
	
    private static final Logger log = LoggerFactory.getLogger(SubscriptionIT.class);
	@Autowired private EmpConnector empConnector;
	@Autowired private MessageHandlerMock messageHandler;
	
	@Autowired private PartnerConnection partnerConnection;

	@Test
	public void testSubscription() throws ConnectionException, InterruptedException {
		assertNotNull(empConnector);
		createInvoiceStatement();
		assertTrue("Did not reach expected count",messageHandler.getLatch().await(10000, TimeUnit.MILLISECONDS));
	}

	private void createInvoiceStatement() throws ConnectionException {
		SObject invoiceStatement = new SObject();
		invoiceStatement.setType("Invoice_Statement__c");
		invoiceStatement.setField("Description__c", "whatever");
		invoiceStatement.setField("Status__c", "Open");
		invoiceStatement.setField("mycurrencyfield__c", 200D);
		invoiceStatement.setField("mynumber__c", 400D);
		invoiceStatement.setField("mypercent__c", 20D);
		invoiceStatement.setField("mydatetime__c", new GregorianCalendar());
		invoiceStatement.setField("mydate__c", new GregorianCalendar());
		SObject[] records = new SObject[1];
		records[0] = invoiceStatement;
		SaveResult[] saveResults = partnerConnection.create(records);
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
