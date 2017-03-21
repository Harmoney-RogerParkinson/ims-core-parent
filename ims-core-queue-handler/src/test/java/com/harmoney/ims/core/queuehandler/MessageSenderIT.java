package com.harmoney.ims.core.queuehandler;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.harmoney.ims.core.database.DatabaseSpringConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { QueueHandlerSpringConfig.class,
		DatabaseSpringConfig.class })
@ActiveProfiles("queue-handler-dev")
public class MessageSenderIT {

	private static final Logger log = LoggerFactory
			.getLogger(MessageSenderIT.class);

	public String queueName;
	@Autowired
	ConfigurableApplicationContext context;
	@Autowired
	ReceiverMock receiver;
	@Autowired
	RabbitTemplate rabbitTemplate;

	@Test
	public void test() throws Exception {
		log.info("Sending message...");
		// {event={createdDate=2017-02-23T00:24:29.976Z, replayId=3,
		// type=created}, sobject={mynumber__c=400.0,
		// mydate__c=2017-02-23T00:00:00.000Z, mypercent__c=20.0,
		// Description__c=whatever, mycurrencyfield__c=200.0,
		// Id=a6fN00000008gmMIAQ, Status__c=Open,
		// mydatetime__c=2017-02-23T00:24:18.000Z, Name=INV-0039}}
		Map<String, Object> map = new HashMap<>();
		Map<String, Object> map1 = new HashMap<>();
		map.put("event", map1);
		map1.put("createdDate", "2017-02-23T00:24:29.976Z");
		map1.put("replayId", new Integer(3));
		map1.put("type", "created");
		Map<String, Object> map2 = new HashMap<>();
		map.put("sobject", map2);
		map2.put("Id", "a6fN00000008giLIAQ");
		map2.put("Name", "INV-0033");
		map2.put("CreatedDate", "2017-02-23T00:00:00.000Z");
		map2.put("loan__Principal_Paid__c", new Double(200D));
		map2.put("loan__Late_Fees_Paid__c", new Double(200D));
		map2.put("loan__Charged_Off_Interest__c", new Double(200D));
		map2.put("Protect_Realised__c", new Double(200D));
		map2.put("loan__Investor_Loan__c", "loan__Investor_Loan__c");
		map2.put("loan__Protect_Principal__c", new Double(200D));
		map2.put("loan__Charged_Off_Principal__c", new Double(200D));
		map2.put("Investor_Txn_Fee__c", new Double(200D));
		map2.put("loan__Interest_Paid__c", new Double(200D));
		map2.put("loan__Rebate_Amount_On_Payoff__c", new Double(200D));
		map2.put("Management_Fee_Realised__c", new Double(200D));
		map2.put("Net_Amount__c", new Double(200D));
		map2.put("loan__Tax__c", new Double(200D));
		map2.put("loan__Total_Service_Charge__c", new Double(200D));
		map2.put("loan__Charged_Off_Fees__c", new Double(200D));
		map2.put("Sales_Commission_Fee_Realised__c", new Double(200D));
		map2.put("loan__Txn_Code__c", new Double(200D));
		
		map2.put("loan__Txn_Type__c", null);
		map2.put("loan__Waived__c", new Boolean(false));
		map2.put("loan__Account__c", "loan__Account__c");
		map2.put("Investment_Order_Status__c", "Investment_Order_Status__c");
		map2.put("loan__Charged_Off_Date__c", "2017-02-23T00:00:00.000Z");
		map2.put("Reverse_Rejected_Date__c", null);
		map2.put("Account_ID__c", "Account_ID__c");
		map2.put("loan__Txn_Code__c", "ACTIVE");
		map2.put("Reversed__c", new Boolean(false));
		map2.put("Rejected__c", new Boolean(false));
		map2.put("transactionType", "ACTIVE");
		rabbitTemplate.convertAndSend(ConfiguredQueueParameters.ILTQUEUE, map);
		assertTrue("Did not reach expected count",
				receiver.getLatch().await(100000, TimeUnit.MILLISECONDS));
		context.close();
	}

}
