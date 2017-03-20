/**
 * 
 */
package com.harmoney.ims.core.databaseloader;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.harmoney.ims.core.database.DatabaseSpringConfig;

/**
 * Create a test database by loading the xml file into the configured target database
 * and then running the balance forward algorithms on it.
 * The intention here is to create a database that the server can run against and
 * service web query requests for verification purposes.
 * 
 * @author Roger Parkinson
 *
 */
public class DatabaseLoaderMain {
	
    private static final String dbLocation = "balanceforward.xml";

	
	public static void main(String[] args) throws Exception {
		
        final AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        // setup configuration
        applicationContext.register(DatabaseLoaderSpringConfig.class);
        applicationContext.register(DatabaseSpringConfig.class);
//        // add CLI property source
//        applicationContext.getEnvironment().getPropertySources()
//                .addLast(new SimpleCommandLinePropertySource(args));

        // setup all the dependencies (refresh) and make them run (start)
        applicationContext.refresh();
        applicationContext.start();
        
        DatabaseLoader databaseLoader = applicationContext.getBean(DatabaseLoader.class);
        databaseLoader.loadDatabase(dbLocation);
        databaseLoader.calculateBalanceForward();
        
        applicationContext.close();
	}

}
