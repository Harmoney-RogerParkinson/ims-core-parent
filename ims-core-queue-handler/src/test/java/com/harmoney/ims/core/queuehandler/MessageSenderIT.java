package com.harmoney.ims.core.queuehandler;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={QueueHandlerSpringConfig.class})
@PropertySource("classpath:test.properties")
public class MessageSenderIT {

    private static final Logger log = LoggerFactory.getLogger(MessageSenderIT.class);
    
	 @Value("${rabbitmq.queue:transaction-queue}")
	 public String queueName;
    @Autowired ConfigurableApplicationContext context;
    @Autowired Receiver receiver;
    @Autowired RabbitTemplate rabbitTemplate;

	@Test
	public void test() throws Exception {
        System.out.println("Sending message...");
        Map<String,Object> map = new HashMap<>();
        Map<String,Object> map2 = new HashMap<>();
        map.put("sobject", map2);
        map2.put("Description__c", "whatever");
        map2.put("Id", "a6fN00000008giLIAQ");
        map2.put("Status__c", "Open");
        map2.put("Name", "INV-0033");
        rabbitTemplate.convertAndSend(queueName, map);
        receiver.getLatch().await(10000, TimeUnit.MILLISECONDS);
        context.close();
        }

}
