package com.harmoney.ims.core.balanceforward;

import static org.junit.Assert.fail;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.harmoney.ims.core.database.DatabaseSpringConfig;
import com.harmoney.ims.core.partner.PartnerConnectionSpringConfig;
import com.harmoney.ims.core.queries.QuerySpringConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { QuerySpringConfig.class, PartnerConnectionSpringConfig.class,DatabaseSpringConfig.class})
public class InvestorLoanTransactionBalanceForwardIT {

	@Test @Ignore
	public void processBalanceForward() {
		fail("Not yet implemented");
	}

}
