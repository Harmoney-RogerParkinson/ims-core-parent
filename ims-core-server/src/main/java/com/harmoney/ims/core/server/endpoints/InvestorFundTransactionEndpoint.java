package com.harmoney.ims.core.server.endpoints;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.harmoney.ims.core.database.InvestorFundTransactionDAO;
import com.harmoney.ims.core.instances.InvestorFundTransaction;
import com.harmoney.ims.core.instances.InvestorFundTransactionRequest;
import com.harmoney.ims.core.instances.InvestorFundTransactionResponse;
import com.harmoney.ims.core.instances.ItemType;
import com.harmoney.ims.core.instances.Transaction;

@Endpoint
public class InvestorFundTransactionEndpoint
{
	private static final String TARGET_NAMESPACE = "http://www.harmoney.com/ims-core";

    @Autowired InvestorFundTransactionDAO investorFundTransactionDAO;

    @PayloadRoot(localPart = "InvestorFundTransactionRequest", namespace = TARGET_NAMESPACE)
	public @ResponsePayload InvestorFundTransactionResponse getInvestorFundTransactionDetails(@RequestPayload InvestorFundTransactionRequest request)
	{
    	Date d = request.getQueryDate();
    	Assert.notNull(d, "Date format could not be parsed. Use YYYY-MM-DD");
    	LocalDate queryDate = d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    	LocalDateTime lastMomentOfLastMonth = investorFundTransactionDAO.getLastMomentOfLastMonth(queryDate);
    	LocalDateTime lastMomentOfMonth = investorFundTransactionDAO.getLastMomentOfMonth(queryDate);

    	InvestorFundTransactionResponse response = new InvestorFundTransactionResponse();
    	List<InvestorFundTransaction> ret = investorFundTransactionDAO.getByAccountDate(lastMomentOfLastMonth, lastMomentOfMonth, request.getAccountId());
    	if (ret.size() > 0) {
    		Transaction lastRecord = ret.get(ret.size()-1);
    		if (lastRecord.getCreatedDate().equals(lastMomentOfMonth) && lastRecord.getTxType() == ItemType.BALANCE_FORWARD) {
    			ret.remove(lastRecord);
    		}
    	}

		response.setInvestorFundTransaction(ret);
		return response;
	}

}