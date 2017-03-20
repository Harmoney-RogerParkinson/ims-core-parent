package com.harmoney.ims.core.server.endpoints;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.harmoney.ims.core.database.AccountDAO;
import com.harmoney.ims.core.instances.Account;
import com.harmoney.ims.core.instances.AccountRequest;
import com.harmoney.ims.core.instances.AccountResponse;

@Endpoint
public class AccountServiceEndpoint
{
	private static final String TARGET_NAMESPACE = "http://www.harmoney.com/ims-core";
    
    @Autowired AccountDAO accountDAO;

	@PayloadRoot(localPart = "AccountRequest", namespace = TARGET_NAMESPACE)
	public @ResponsePayload AccountResponse getAccountDetails(@RequestPayload AccountRequest request)
	{
		AccountResponse response = new AccountResponse();
		
		Account account = accountDAO.getByHarmoneyAccountNumber(request.getHarmoneyAccountNumber());
		response.setAccount(account);
		return response;
	}

}