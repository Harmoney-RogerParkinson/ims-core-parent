package com.harmoney.ims.core.queueprocessor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.harmoney.ims.core.database.AmortizationScheduleDAO;
import com.harmoney.ims.core.database.ConvertUtils;
import com.harmoney.ims.core.database.InvestmentOrderDAO;
import com.harmoney.ims.core.database.ProtectRealisedRevenueDAO;
import com.harmoney.ims.core.database.UnpackHelper;
import com.harmoney.ims.core.database.descriptors.Result;
import com.harmoney.ims.core.instances.AmortizationSchedule;
import com.harmoney.ims.core.instances.ProtectRealisedRevenue;
import com.harmoney.ims.core.queries.AmortizationScheduleQuery;
import com.harmoney.ims.core.queries.InvestmentOrderQuery;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;

@Component
public class AmortizationScheduleProcessor {

    private static final Logger log = LoggerFactory.getLogger(InvestmentOrderProcessor.class);
	
	@Autowired private AmortizationScheduleDAO amortizationScheduleDAO;
	@Autowired private ProtectRealisedRevenueDAO protectRealisedRevenueDAO;
	@Autowired private PartnerConnectionWrapper partnerConnection;
	@Autowired private UnpackHelper unpackHelper;
	public static BigDecimal BIG_DECIMAL_ZERO_SCALED = BigDecimal.ZERO.setScale(2);

	@Transactional
	public void processQuery(SObject[] records) {
		for (SObject sobject: records) {
			Result result = amortizationScheduleDAO.createOrUpdate(sobject);
			log.debug("Result: {}",result);
		}
	}

	/**
	 * The Loan Account Status was changed to some form of 'closed', possibly with a waiver flag
	 * Find the remaining Amortization records and accumulate them to create a new PRR set.
	 * 
	 * @param loanAccountId
	 * @param statusWaived
	 * @param createdDate 
	 * @throws ConnectionException
	 */
	@Transactional
	public void loanAccountStatusClosed(String loanAccountId, boolean statusWaived, String eventDate) {
		String queryString = AmortizationScheduleQuery.SOQL
				+ "WHERE loan__Loan_Account__c = '"+loanAccountId+"' and loan__Due_Date__c >='"+eventDate+"' order by loan__Due_Date__c";

		SObject[] records;
		try {
			records = partnerConnection.query(queryString);
		} catch (ConnectionException e) {
			log.error(e.getMessage());
			throw new ProtectRealisedException(e);
		}
		if (records.length == 0) {
			String message = "failed to find any Amortization Schedule entry for "+loanAccountId+" "+eventDate+". Ignoring.";
			log.error(message);
			throw new ProtectRealisedException(message);
		}
		AmortizationSchedule total = new AmortizationSchedule();
		total.setAccountId(loanAccountId);
		for (SObject sobject: records) {
			AmortizationSchedule amortizationSchedule = amortizationScheduleDAO.unpack(sobject);
			amortizationScheduleDAO.accumulate(amortizationSchedule,total);
			if (total.getDueDate()==null) {
				total.setDueDate(amortizationSchedule.getDueDate());
			}
		}
		createOrUpdateProtectRealisedRevenue(loanAccountId,total, true, statusWaived, ConvertUtils.parseDate(eventDate));
	}
	/**
	 * Bill record had PaymentSatisfied flag set to false
	 * Go figure the ProtectRealised values and update them to zero
	 * 
	 * @param loanAccountId
	 * @param waiverApplied
	 * @param dueDate
	 * @throws ConnectionException 
	 */
	@Transactional
	public void billPaymentUnsatisfied(String loanAccountId,
			boolean waiverApplied, Date dueDate, Date eventDate) {
		AmortizationSchedule amortizationSchedule = getAmortizationSchedule(loanAccountId, dueDate);
		amortizationSchedule.setProtectRealised(new BigDecimal(0));
		createOrUpdateProtectRealisedRevenue(loanAccountId,amortizationSchedule, true, true,null);
	}

	/**
	 * Bill record had PaymentSatisfied flag set to true
	 * Go figure the ProtectRealised values and update them
	 * 
	 * @param loanAccountId
	 * @param waiverApplied
	 * @param dueDate
	 * @throws ConnectionException
	 */
	@Transactional
	public void billPaymentSatisfied(String loanAccountId,
			boolean waiverApplied, Date dueDate, Date eventDate) {
		AmortizationSchedule amortizationSchedule = getAmortizationSchedule(loanAccountId, dueDate);
		createOrUpdateProtectRealisedRevenue(loanAccountId,amortizationSchedule, true, waiverApplied,eventDate);
	}
	

	/**
	 * A new Bill was created. Find the equivalent record in the Amortization Schedule and use that to create
	 * the ProtectRealisedRevenue records.
	 * 
	 * @param loanAccountId
	 * @param dueDate
	 * @throws ConnectionException
	 */
	@Transactional
	public void billCreated(String loanAccountId, Date dueDate, Date eventDate) {
		AmortizationSchedule amortizationSchedule = getAmortizationSchedule(loanAccountId, dueDate);
		createOrUpdateProtectRealisedRevenue(loanAccountId,amortizationSchedule, eventDate);
	}

	/**
	 * The Loan Account status has just been changed to 'Active - Good Standing'
	 * Create the initial ProtectRealisedRevenue records but only populate them with fees.
     * -- discarded in favour of doing it on Bill creation so this does nothing.
	 * 
	 * @param loanAccountId
	 * @throws ConnectionException 
	 */
//	@Transactional
	public void loanAccountStatusActive(String loanAccountId) {
//		String queryString = AmortizationScheduleQuery.SOQL
//				+ "WHERE loan__Loan_Account__c = '"+loanAccountId+"' order by loan__Due_Date__c";
//
//		SObject[] records;
//		try {
//			records = partnerConnection.query(queryString);
//		} catch (ConnectionException e) {
//			log.error(e.getMessage());
//			throw new ProtectRealisedException(e);
//		}
//		if (records.length == 0) {
//			String message = "failed to find any Amortization Schedule entry for "+loanAccountId+". Ignoring.";
//			log.error(message);
//			throw new ProtectRealisedException(message);
//		}
//		AmortizationSchedule amortizationSchedule = amortizationScheduleDAO.unpack(records[0]); // only interested in the first one
//		createOrUpdateProtectRealisedRevenue(loanAccountId,amortizationSchedule);
	}
	
	/**
	 * Create a PRR record, but only update the fees fields unless the flags are set
	 * Check if it is already there, and just update if it is.
	 * 
	 * @param loanAccountId
	 * @param amortizationSchedule
	 * @throws ConnectionException
	 */
	private void createOrUpdateProtectRealisedRevenue(String loanAccountId, AmortizationSchedule amortizationSchedule, boolean full, boolean waiver, Date eventDate) {
		SObject[] records;
		try {
			records = partnerConnection.query(InvestmentOrderQuery.SOQL2
					+ "WHERE loan__Account__c = '"+loanAccountId+"'");
		} catch (ConnectionException e) {
			log.error(e.getMessage());
			throw new ProtectRealisedException(e);
		}
		for (SObject sobject: records) {
			String investmentOrderId = (String)sobject.getField("Id");
			String investmentOrderLoanShare = (String)sobject.getField("loan__Share__c");
			ProtectRealisedRevenue protectRealisedRevenue;
			protectRealisedRevenue = protectRealisedRevenueDAO.getByInvestmentOrderIdAndDate(investmentOrderId,amortizationSchedule.getDueDate());
			if (protectRealisedRevenue == null) {
				protectRealisedRevenue = new ProtectRealisedRevenue();
				protectRealisedRevenue.setInvestmentOrderId(investmentOrderId);
				protectRealisedRevenue.setDueDate(amortizationSchedule.getDueDate());
				protectRealisedRevenueDAO.create(protectRealisedRevenue);
			}
			BigDecimal proportion = new BigDecimal(investmentOrderLoanShare);
			protectRealisedRevenue.setManagementFeeRealised(safeDivide(amortizationSchedule.getManagementFeeRealised(),proportion));
			protectRealisedRevenue.setSalesCommissionFeeRealised(safeDivide(amortizationSchedule.getSalesCommissionRealised(),proportion));
			if (full) {
				protectRealisedRevenue.setProtectRealised(safeDivide(amortizationSchedule.getProtectRealised(),proportion));
				protectRealisedRevenue.setProtectRealisedDate(eventDate);
				if (waiver) {
					protectRealisedRevenue.setProtectWaived(protectRealisedRevenue.getProtectRealised());
				} else {
					protectRealisedRevenue.setProtectWaived(BIG_DECIMAL_ZERO_SCALED);
				}
			} else {
				protectRealisedRevenue.setProtectRealised(BIG_DECIMAL_ZERO_SCALED);
				protectRealisedRevenue.setProtectWaived(BIG_DECIMAL_ZERO_SCALED);
				protectRealisedRevenue.setProtectRealisedDate(null);
			}
			protectRealisedRevenueDAO.merge(protectRealisedRevenue);
		}
	}
	private BigDecimal safeDivide(BigDecimal value, BigDecimal proportion) {
		if (value == null || proportion == null || proportion.doubleValue() < 0.001D) {
			return BIG_DECIMAL_ZERO_SCALED;
		}
		try {
			return value.divide(proportion,2, RoundingMode.HALF_UP);
		} catch (ArithmeticException e) {
			throw e;
		}
	}
	private void createOrUpdateProtectRealisedRevenue(String loanAccountId, AmortizationSchedule amortizationSchedule, Date eventDate) {
		createOrUpdateProtectRealisedRevenue(loanAccountId, amortizationSchedule, false, false, eventDate);
	}

	private AmortizationSchedule getAmortizationSchedule(String loanAccountId, Date dueDate) {
		String queryString = AmortizationScheduleQuery.SOQL
				+ "WHERE loan__Loan_Account__c = '"+loanAccountId+"' and loan__Due_Date__c = '"+ConvertUtils.printDate(dueDate)+"'";

		SObject[] records;
		try {
			records = partnerConnection.query(queryString);
		} catch (ConnectionException e) {
			log.error(e.getMessage());
			throw new ProtectRealisedException(e);
		}
		if (records.length != 1) {
			String message = "failed to find exactly one Amortization Schedule entry for "+loanAccountId+" "+ConvertUtils.printDate(dueDate)+", found "+records.length+". Ignoring.";
			log.error(message);
			throw new ProtectRealisedException(message);
		}
		AmortizationSchedule amortizationSchedule = amortizationScheduleDAO.unpack(records[0]);
		return amortizationSchedule;
	}


}
