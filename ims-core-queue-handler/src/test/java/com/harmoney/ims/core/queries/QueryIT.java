package com.harmoney.ims.core.queries;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.harmoney.ims.core.database.DatabaseSpringConfig;
import com.harmoney.ims.core.partner.PartnerConnectionSpringConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource("/QueryTest.properties")
@ContextConfiguration(classes = { QuerySpringConfig.class, PartnerConnectionSpringConfig.class,DatabaseSpringConfig.class})
public class QueryIT {

	private static final Logger log = LoggerFactory
			.getLogger(QueryIT.class);

	@Autowired
	ConfigurableApplicationContext context;
	@Autowired AccountQuery accountquery;
	@Autowired InvestmentOrderQuery investmentOrderquery;
    @Value("${database.hbm2ddl.auto:}")
    private String hbm2ddlauto;
	@Value("${database.dialect:org.hibernate.dialect.PostgreSQLDialect}")
	private String databaseDialect;

	@Test
	public void testAccountSummaryQuery() throws Exception {
		
		log.info("Database hbm2ddlauto={} database dialect {}",hbm2ddlauto,databaseDialect);
		accountquery.doQuery();
	}

	@Test
	public void testInvestmentOrderQuery() throws Exception {
		
		log.info("Database hbm2ddlauto={} database dialect {}",hbm2ddlauto,databaseDialect);
		investmentOrderquery.doQuery();
	}

}
