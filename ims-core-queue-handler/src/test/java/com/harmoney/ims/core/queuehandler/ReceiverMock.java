package com.harmoney.ims.core.queuehandler;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import nz.co.senanque.madura.ampq.AMPQReceiver;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.harmoney.ims.core.database.ConvertUtils;
import com.harmoney.ims.core.database.InvestorLoanTransactionDAO;
import com.harmoney.ims.core.database.descriptors.Result;
import com.harmoney.ims.core.instances.InvestorLoanTransaction;

/**
 * @author Roger Parkinson
 *
 */
@Component
@Profile("queue-handler-dev")
public class ReceiverMock {

    private static final Logger log = LoggerFactory.getLogger(ReceiverMock.class);
    
    private CountDownLatch latch = new CountDownLatch(1);
    @Autowired private InvestorLoanTransactionDAO investorLoanTransactionDAO;

    @AMPQReceiver(queueName="ilt-queue")
    public void receiveMessage(Map<String, Map<String, Object>> message) {
        log.debug("Received <{}>", message);
        InvestorLoanTransaction target = new InvestorLoanTransaction();
        Result result = investorLoanTransactionDAO.unpackMessage(message, target);
        log.debug("{}",result);
        Assert.assertEquals("a6fN00000008giLIAQ",target.getId());
        Assert.assertEquals(LocalDateTime.parse("2017-02-23T00:00"),ConvertUtils.convertTolocalDateTime(target.getCreatedDate()));
        Assert.assertEquals(new BigDecimal(200D).longValue(),target.getPrincipalPaid().longValue());
        latch.countDown();
    }

    public CountDownLatch getLatch() {
        return latch;
    }

}