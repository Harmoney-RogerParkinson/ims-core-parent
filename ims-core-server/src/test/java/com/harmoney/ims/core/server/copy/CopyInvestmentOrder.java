package com.harmoney.ims.core.server.copy;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.harmoney.ims.core.database.DatabaseSpringConfig;
import com.harmoney.ims.core.database.InvestmentOrderDAO;
import com.harmoney.ims.core.partner.PartnerConnectionSpringConfig;
import com.harmoney.ims.core.server.test.ILTServerIT;
import com.sforce.ws.ConnectionException;

@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource("/test2.properties")
@ContextConfiguration(classes = { PartnerConnectionSpringConfig.class,
		DatabaseSpringConfig.class, CopyDBSpringConfig.class })
@ActiveProfiles({ "message-processor-dev", "server-dev" })
public class CopyInvestmentOrder {

	private static final Logger log = LoggerFactory.getLogger(ILTServerIT.class);

	@Autowired
	private InvestmentOrderDAO investmentOrderDAO;
	@Autowired
	private GenericCopier genericCopier;

	@Test
	public void copyInvestmentOrder() throws ConnectionException {

		genericCopier.copy(investmentOrderDAO);
	}

}
