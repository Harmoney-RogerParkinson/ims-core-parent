package com.harmoney.ims.core.server.endpoints;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.harmoney.ims.core.instances.Account;
import com.harmoney.ims.core.instances.AccountRequest;
import com.harmoney.ims.core.instances.AccountResponse;

/**
 * The Class AccountService.
 */
@Endpoint
public class AccountServiceEndpoint
{
	private static final String TARGET_NAMESPACE = "http://www.harmoney.com/ims-core";
    private static final Logger log = LoggerFactory.getLogger(AccountServiceEndpoint.class);

	/**
	 * Gets the account details.
	 *
	 * @param accountNumber the account number
	 * @return the account details
	 */
	@PayloadRoot(localPart = "AccountRequest", namespace = TARGET_NAMESPACE)
	public @ResponsePayload AccountResponse getAccountDetails(@RequestPayload AccountRequest request)
	{
		AccountResponse response = new AccountResponse();

		Account account = new Account();
		account.setHarmoneyAccountNumber(request.getHarmoneyAccountNumber());
		response.setAccount(account);
		return response;
	}
	@PostConstruct
	public void init() {
		log.debug("initialising");
	}

}