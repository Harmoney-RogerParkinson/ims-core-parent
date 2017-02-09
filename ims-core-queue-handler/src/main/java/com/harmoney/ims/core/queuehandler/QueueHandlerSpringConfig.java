package com.harmoney.ims.core.queuehandler;

import java.util.Arrays;

import javax.jms.ConnectionFactory;
import javax.jms.MessageListener;

import org.apache.activemq.spring.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.SimpleMessageConverter;
import org.springframework.jms.support.destination.DestinationResolver;
import org.springframework.jms.support.destination.DynamicDestinationResolver;

@Configuration
@EnableJms
@ComponentScan("com.harmoney.ims.core.queuehandler")
@PropertySource(value={"classpath:test.properties"},ignoreResourceNotFound = true)
public class QueueHandlerSpringConfig {

	@Value("${broker.url:tcp://localhost:61616}")
	public String brokerURL;
	@Value("${queue.name:transaction-queue}")
	public String queueName;

    @Autowired MessageListener messageListener;
     
    @Bean(name="jmsListenerContainerFactory")
    public DefaultJmsListenerContainerFactory myJmsListenerContainerFactory() {
      DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
      factory.setConnectionFactory(connectionFactory());
      factory.setDestinationResolver(destinationResolver());
      factory.setConcurrency("5");
      return factory;
    }
    
    @Bean
    public ConnectionFactory connectionFactory(){
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL(brokerURL);
        connectionFactory.setTrustedPackages(Arrays.asList("com.harmoney.ims.core.queuehandler"));
        return connectionFactory;
    }
    /*
     * Optionally you can use cached connection factory if performance is a big concern.
     */
 
//    @Bean
//    public ConnectionFactory cachingConnectionFactory(){
//        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
//        connectionFactory.setTargetConnectionFactory(connectionFactory());
//        connectionFactory.setSessionCacheSize(10);
//        return connectionFactory;
//    }
 
    @Bean
    public DestinationResolver destinationResolver() {
    	return new DynamicDestinationResolver();
    }
    /*
     * Used for Sending Messages.
     */
    @Bean
    public JmsTemplate jmsTemplate(){
        JmsTemplate template = new JmsTemplate();
        template.setConnectionFactory(connectionFactory());
        template.setDefaultDestinationName(queueName);
        return template;
    }
     
    @Bean
    MessageConverter converter(){
        return new SimpleMessageConverter();
    }
}
