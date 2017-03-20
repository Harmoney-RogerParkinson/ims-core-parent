package com.harmoney.ims.core.server.endpoints;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.harmoney.ims.core.database.InvestmentOrderDAO;
import com.harmoney.ims.core.instances.InvestmentOrderRequest;
import com.harmoney.ims.core.instances.InvestmentOrderResponse;

@Endpoint
public class InvestmentOrderEndpoint
{
	private static final String TARGET_NAMESPACE = "http://www.harmoney.com/ims-core";

    @Autowired InvestmentOrderDAO investmentOrderDAO;
    
	@PayloadRoot(localPart = "InvestmentOrderRequest", namespace = TARGET_NAMESPACE)
	public @ResponsePayload InvestmentOrderResponse getInvestmentOrderDetails(@RequestPayload InvestmentOrderRequest request)
	{
		InvestmentOrderResponse response = new InvestmentOrderResponse();
		response.setInvestmentOrder(investmentOrderDAO.getAll());
		return response;
	}

}