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

import com.harmoney.ims.core.database.InvestorLoanTransactionDAO;
import com.harmoney.ims.core.instances.InvestorLoanTransaction;
import com.harmoney.ims.core.instances.InvestorLoanTransactionRequest;
import com.harmoney.ims.core.instances.InvestorLoanTransactionResponse;
import com.harmoney.ims.core.instances.ItemType;
import com.harmoney.ims.core.instances.Transaction;

@Endpoint
public class InvestorLoanTransactionEndpoint
{
	private static final String TARGET_NAMESPACE = "http://www.harmoney.com/ims-core";

    @Autowired InvestorLoanTransactionDAO investorLoanTransactionDAO;

    @PayloadRoot(localPart = "InvestorLoanTransactionRequest", namespace = TARGET_NAMESPACE)
	public @ResponsePayload InvestorLoanTransactionResponse getInvestorLoanTransactionDetails(@RequestPayload InvestorLoanTransactionRequest request)
	{
    	Date d = request.getQueryDate();
    	Assert.notNull(d, "Date format could not be parsed. Use YYYY-MM-DD");
    	LocalDate queryDate = d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    	LocalDateTime lastMomentOfLastMonth = investorLoanTransactionDAO.getLastMomentOfLastMonth(queryDate);
    	LocalDateTime lastMomentOfMonth = investorLoanTransactionDAO.getLastMomentOfMonth(queryDate);

    	InvestorLoanTransactionResponse response = new InvestorLoanTransactionResponse();
    	List<InvestorLoanTransaction> ret = investorLoanTransactionDAO.getByAccountDate(lastMomentOfLastMonth, lastMomentOfMonth, request.getAccountId());
    	if (ret.size() > 0) {
    		Transaction lastRecord = ret.get(ret.size()-1);
    		if (lastRecord.getCreatedDate().equals(lastMomentOfMonth) && lastRecord.getTxType() == ItemType.BALANCE_FORWARD) {
    			ret.remove(lastRecord);
    		}
    	}

		response.setInvestorLoanTransaction(ret);
		return response;
	}

}