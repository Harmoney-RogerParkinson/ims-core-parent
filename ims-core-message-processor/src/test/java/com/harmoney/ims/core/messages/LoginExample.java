/* 
 * Copyright (c) 2016, salesforce.com, inc.
 * All rights reserved.
 * Licensed under the BSD 3-Clause license. 
 * For full license text, see LICENSE.TXT file in the repo root  or https://opensource.org/licenses/BSD-3-Clause
 */
package com.harmoney.ims.core.messages;

import static com.salesforce.emp.connector.LoginHelper.login;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.salesforce.emp.connector.BayeuxParameters;
import com.salesforce.emp.connector.EmpConnector;
import com.salesforce.emp.connector.TopicSubscription;

/**
 * An example of using the EMP connector using login credentials
 *
 * @author hal.hildebrand
 * @since 202
 */
public class LoginExample {
    private static final Logger log = LoggerFactory.getLogger(LoginExample.class);
    public static void main(String[] argv) throws Exception {
        if (argv.length < 3 || argv.length > 4) {
            System.err.println("Usage: LoginExample username password topic [replayFrom]");
            System.exit(1);
        }
        long replayFrom = EmpConnector.REPLAY_FROM_EARLIEST;
        if (argv.length == 4) {
            replayFrom = Long.parseLong(argv[3]);
        }

        BayeuxParameters params;
        try {
            params = login(argv[0], argv[1]);
            log.debug("login successful");
        } catch (Exception e) {
            e.printStackTrace(System.err);
            System.exit(1);
            throw e;
        } 

        Consumer<Map<String, Object>> consumer = event -> System.out.println(String.format("Received:\n%s", event));
        EmpConnector connector = new EmpConnector(params);

        connector.start().get(5, TimeUnit.SECONDS);
        log.debug("connector started");


        TopicSubscription subscription = connector.subscribe(argv[2], replayFrom, consumer).get(5, TimeUnit.SECONDS);
        log.debug("subscription created");

        System.out.println(String.format("Subscribed: %s", subscription));
    }
}
