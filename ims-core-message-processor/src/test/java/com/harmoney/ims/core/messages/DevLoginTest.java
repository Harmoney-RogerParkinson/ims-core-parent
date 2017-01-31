package com.harmoney.ims.core.messages;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.harmoney.ims.core.messages.SpringConfig;
import com.salesforce.emp.connector.TopicSubscription;

/**
 * @author Roger Parkinson
 * 
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={SpringConfig.class})
@PropertySource("classpath:test.properties")
@ActiveProfiles("dev")
public class DevLoginTest {
	
	@Autowired private TopicSubscription topicSubscription;

	@Test
	public void testSubscription() {
		assertNotNull(topicSubscription);
	}
}
