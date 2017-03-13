package com.harmoney.ims.core.queries;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.harmoney.ims.core.database.DatabaseSpringConfig;
import com.harmoney.ims.core.partner.PartnerConnectionSpringConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { QuerySpringConfig.class, PartnerConnectionSpringConfig.class,DatabaseSpringConfig.class})
public class QueryIT {

	private static final Logger log = LoggerFactory
			.getLogger(QueryIT.class);

	@Autowired
	ConfigurableApplicationContext context;
	@Autowired AccountSummaryQuery accountSummaryquery;
	@Autowired InvestmentOrderQuery investmentOrderquery;

	@Test
	public void testAccountSummaryQuery() throws Exception {
		
		accountSummaryquery.doQuery();
	}

	@Test
	public void testInvestmentOrderQuery() throws Exception {
		
		investmentOrderquery.doQuery();
	}

}
