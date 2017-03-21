/**
 * 
 */
package com.harmoney.ims.core.server.test;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.harmoney.ims.core.database.DatabaseSpringConfig;

/**
 * Run a test sequence with the schedlock facility turned on.
 * This is to verify that multiple servers won't run the same scheduled query at once.
 * 
 * @author Roger Parkinson
 *
 */
public class TestSchedlockMain {
	
	
	public static void main(String[] args) throws Exception {
		
        final AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        // setup configuration
        applicationContext.register(TestSchedlockSpringConfig.class);
        applicationContext.register(DatabaseSpringConfig.class);
        applicationContext.refresh();
        applicationContext.start();
        
        Thread.sleep(200000);
        applicationContext.close();
	}

}
