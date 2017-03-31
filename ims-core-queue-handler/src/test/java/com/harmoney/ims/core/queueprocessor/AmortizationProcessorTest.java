/**
 * 
 */
package com.harmoney.ims.core.queueprocessor;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.harmoney.ims.core.database.DatabaseSpringConfig;
import com.harmoney.ims.core.database.LoanAccountDAO;
import com.harmoney.ims.core.database.ProtectRealisedRevenueDAO;
import com.harmoney.ims.core.instances.LoanAccount;
import com.harmoney.ims.core.instances.LoanAccountStatus;
import com.harmoney.ims.core.instances.ProtectRealisedRevenue;

/**
 * @author Roger Parkinson
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { DatabaseSpringConfig.class,AmortizationSpringConfig.class })
@ActiveProfiles("queue-handler-dev")
@TestPropertySource("/H2Test.properties")
@Transactional // needed for rollback
@Rollback // rollback all db changes
public class AmortizationProcessorTest {
	
	@Autowired BillProcessor billProcessor;
	@Autowired LoanAccountProcessor loanAccountProcessor;
	@Autowired ProtectRealisedRevenueDAO protectRealisedRevenueDAO;
	@Autowired private LoanAccountDAO loanAccountDAO;

	/**
	 * Not quite so simple. Create a loan account and change to active.
	 * The create a Bill for its first month (which creates PRRs for its *second* month)
	 * Then create a bill for the first month again (which does nothing 'cos it is already there)
	 * Then update bill for month 1 with satisfied 
	 * New bill for month 2 (unsatisfied) which creates PRRs for month 3
	 * Finally close the loan which forces the unsatisfied months (2 & 3) to be summed into month 3 and satisfied.
	 */
	@Test
	public void testSimpleSequence() {
		
		List<ProtectRealisedRevenue> createdPRRs = protectRealisedRevenueDAO.getAll();
		assertEquals(0,createdPRRs.size());
		// Loan Account is created initially with status Approved.
		// Does very little but it does create the IMS loan account.
		loanAccountProcessor.receiveMessage(getMap(
				"createdDate=2016-01-01T22:47:15.855Z, replayId=207, type=created",
				"loan__Loan_Status__c=Approved, Id=001p0000001oGUrAAM, harMoney_Account_Number__c=C0284207, Name=LAI-00033933, Waived__c=false"));
		
		LoanAccount loanAccount = loanAccountDAO.getByHarmoneyAccountNumber("C0284207");
		assertEquals(LoanAccountStatus.APPROVED,loanAccount.getStatus());
		
		// Change the Loan Account status to Active - Good Standing
		// That creates 3 PRRs and a Bill for month 1
		loanAccountProcessor.receiveMessage(getMap(
				"createdDate=2016-01-02T22:47:15.855Z, replayId=207, type=updated",
				"loan__Loan_Status__c=Active - Good Standing, Id=001p0000001oGUrAAM, harMoney_Account_Number__c=C0284207, Name=LAI-00033933, Waived__c=false"));
		loanAccount = loanAccountDAO.getByHarmoneyAccountNumber("C0284207");
		assertEquals(LoanAccountStatus.ACTIVE_GOOD_STANDING,loanAccount.getStatus());
		new CreatedPRRsDTO(protectRealisedRevenueDAO.getAll(),
				3,		// size
				0.00,	// protect waived
				0.00,	// protect realized
				0.72,	// management fee realized
				0.52,	// sales commission fee realized
				true	// all dates are null
				);	
		
		// New Bill is created with satisfied=false. PRRs created for the *following* month ie 2016-02
		billProcessor.receiveMessage(getMap(
				"createdDate=2016-01-27T22:10:02.789Z, replayId=400, type=created",
				"loan__Payment_Satisfied__c=false, loan__Loan_Account__c=001p0000001oGUrAAM, CreatedDate=2016-01-27T01:26:22.000Z, loan__Due_Amt__c=353.77, loan__waiver_applied__c=false, loan__Due_Date__c=2016-01-27T00:00:00.000Z, Id=a4np0000000000WAAQ, loan__Transaction_Date__c=2016-01-27T00:00:00.000Z, Name=PCN-0000041405"));
		new CreatedPRRsDTO(protectRealisedRevenueDAO.getAll(),
				6,		// size
				0.00,	// protect waived
				0.00,	// protect realized
				1.44,	// management fee realized
				1.04,	// sales commission fee realized
				true	// all dates are null
				);	

		// Update the bill with satisfied=true
		// No new records but protect realized is updated.
		billProcessor.receiveMessage(getMap(
				"createdDate=2016-01-27T22:10:02.789Z, replayId=400, type=updated",
				"loan__Payment_Satisfied__c=true, loan__Loan_Account__c=001p0000001oGUrAAM, CreatedDate=2016-01-27T01:26:22.000Z, loan__Due_Amt__c=353.77, loan__waiver_applied__c=false, loan__Due_Date__c=2016-01-27T00:00:00.000Z, Id=a4np0000000000WAAQ, loan__Transaction_Date__c=2016-02-27T00:00:00.000Z, Name=PCN-0000041405"));
		// Satisfying the Bill updates its PRR with a ProtectRealised figure, the other Management Fee
		// remains unchanged.
		new CreatedPRRsDTO(protectRealisedRevenueDAO.getAll(),
				6,		// size
				0.00,	// protect waived
				2.27,	// protect realized
				1.44,	// management fee realized
				1.04,	// sales commission fee realized
				false	// all dates are null
				);	

		// New Bill is created with satisfied=false. The bill is created for the *next* month, ie 2016-03
		billProcessor.receiveMessage(getMap(
				"createdDate=2016-02-27T22:10:02.789Z, replayId=400, type=created",
				"loan__Payment_Satisfied__c=false, loan__Loan_Account__c=001p0000001oGUrAAM, CreatedDate=2016-02-27T01:26:22.000Z, loan__Due_Amt__c=353.77, loan__waiver_applied__c=false, loan__Due_Date__c=2016-02-27T00:00:00.000Z, Id=a4np0000000000WAAQ, loan__Transaction_Date__c=2016-02-27T00:00:00.000Z, Name=PCN-0000041405"));
		new CreatedPRRsDTO(protectRealisedRevenueDAO.getAll(),
				9,		// size
				0.00,	// protect waived
				2.27,	// protect realized
				2.16,	// management fee realized
				1.56,	// sales commission fee realized
				false	// all dates are null
				);	

		// Change the Loan Account status to Closed-Obligations met
		loanAccountProcessor.receiveMessage(getMap(
				"createdDate=2016-04-27T22:47:15.855Z, replayId=207, type=updated",
				"loan__Loan_Status__c=Closed - Obligations met, Id=001p0000001oGUrAAM, harMoney_Account_Number__c=C0284207, Name=LAI-00033933, Waived__c=false"));
		// By this time there should be PRR records for this loan and the loan itself is closed.
		loanAccount = loanAccountDAO.getByHarmoneyAccountNumber("C0284207");
		assertEquals(LoanAccountStatus.CLOSED_OBLIGATIONS_MET,loanAccount.getStatus());
		// Closing the loan creates another PRR set which is the sum of the remaining amortisation
		new CreatedPRRsDTO(protectRealisedRevenueDAO.getAll(),
				9,		// size
				0.00,	// protect waived
				6.82,	// protect realized
				2.16,	// management fee realized
				1.56,	// sales commission fee realized
				false	// all dates are null
				);	
	}
	
	/**
	 * Creating a Bill with initial status of satisfied may not come up but it is tested here anyway
	 */
	@Test
	public void testCreateSatisfiedBill() {
		
		List<ProtectRealisedRevenue> createdPRRs = protectRealisedRevenueDAO.getAll();
		assertEquals(0,createdPRRs.size());
		// Loan Account is created initially with status Approved.
		// Does very little but it does create the IMS loan account.
		loanAccountProcessor.receiveMessage(getMap(
				"createdDate=2016-01-01T22:47:15.855Z, replayId=207, type=created",
				"loan__Loan_Status__c=Approved, Id=001p0000001oGUrAAM, harMoney_Account_Number__c=C0284207, Name=LAI-00033933, Waived__c=false"));
		
		LoanAccount loanAccount = loanAccountDAO.getByHarmoneyAccountNumber("C0284207");
		assertEquals(LoanAccountStatus.APPROVED,loanAccount.getStatus());
		
		// Change the Loan Account status to Active - Good Standing
		loanAccountProcessor.receiveMessage(getMap(
				"createdDate=2016-01-02T22:47:15.855Z, replayId=207, type=updated",
				"loan__Loan_Status__c=Active - Good Standing, Id=001p0000001oGUrAAM, harMoney_Account_Number__c=C0284207, Name=LAI-00033933, Waived__c=false"));
		loanAccount = loanAccountDAO.getByHarmoneyAccountNumber("C0284207");
		assertEquals(LoanAccountStatus.ACTIVE_GOOD_STANDING,loanAccount.getStatus());
		new CreatedPRRsDTO(protectRealisedRevenueDAO.getAll(),
				3,		// size
				0.00,	// protect waived
				0.00,	// protect realized
				0.72,	// management fee realised
				0.52,	// sales commision fee realised
				true	// all dates are null
				);	
		
		// New Bill is created with satisfied=true
		billProcessor.receiveMessage(getMap(
				"createdDate=2016-02-27T22:10:02.789Z, replayId=400, type=created",
				"loan__Payment_Satisfied__c=true, loan__Loan_Account__c=001p0000001oGUrAAM, CreatedDate=2016-02-27T01:26:22.000Z, loan__Due_Amt__c=353.77, loan__waiver_applied__c=false, loan__Due_Date__c=2016-02-27T00:00:00.000Z, Id=a4np0000000000WAAQ, loan__Transaction_Date__c=2016-02-27T00:00:00.000Z, Name=PCN-0000041405"));
		// 9 PRRs but since this was a satisfied Bill the ProtectRealised is already added
		// to the first set, Fees are in all 6.
		new CreatedPRRsDTO(protectRealisedRevenueDAO.getAll(),
				9,		// size
				0.00,	// protect waived
				2.27,	// protect realized
				2.16,	// management fee realised
				1.56,	// sales commision fee realised
				false	// all dates are null
				);	

		// Update the bill with satisfied=true
		billProcessor.receiveMessage(getMap(
				"createdDate=2016-02-27T22:10:02.789Z, replayId=400, type=created",
				"loan__Payment_Satisfied__c=true, loan__Loan_Account__c=001p0000001oGUrAAM, CreatedDate=2016-02-27T01:26:22.000Z, loan__Due_Amt__c=353.77, loan__waiver_applied__c=false, loan__Due_Date__c=2016-02-27T00:00:00.000Z, Id=a4np0000000000WAAQ, loan__Transaction_Date__c=2016-02-27T00:00:00.000Z, Name=PCN-0000041405"));
		// Satisfying this Bill changes nothing 'cos it was already satisfied
		new CreatedPRRsDTO(protectRealisedRevenueDAO.getAll(),
				9,		// size
				0.00,	// protect waived
				2.27,	// protect realized
				2.16,	// management fee realised
				1.56,	// sales commision fee realised
				false	// all dates are null
				);	

		// Change the Loan Account status to Closed-Obligations met
		loanAccountProcessor.receiveMessage(getMap(
				"createdDate=2016-04-27T22:47:15.855Z, replayId=207, type=updated",
				"loan__Loan_Status__c=Closed - Obligations met, Id=001p0000001oGUrAAM, harMoney_Account_Number__c=C0284207, Name=LAI-00033933, Waived__c=false"));
		// By this time there should be PRR records for this loan and the loan itself is closed.
		loanAccount = loanAccountDAO.getByHarmoneyAccountNumber("C0284207");
		assertEquals(LoanAccountStatus.CLOSED_OBLIGATIONS_MET,loanAccount.getStatus());
		// Closing the loan creates another PRR which is the sum of the remaining amortisation
		new CreatedPRRsDTO(protectRealisedRevenueDAO.getAll(),
				12,		// size
				0.00,	// protect waived
				6.82,	// protect realized
				3.61,	// management fee realised
				2.59,	// sales commision fee realised
				false	// all dates are null
				);	
	}
	
	/**
	 * Creating Satisfy a Bill with waiver creates one PRR with waiver, but the others remain unwaivered.
	 */
	@Test
	public void testWaiverBill() {
		
		List<ProtectRealisedRevenue> createdPRRs = protectRealisedRevenueDAO.getAll();
		assertEquals(0,createdPRRs.size());
		// Loan Account is created initially with status Approved.
		// Does very little but it does create the IMS loan account.
		loanAccountProcessor.receiveMessage(getMap(
				"createdDate=2016-01-01T22:47:15.855Z, replayId=207, type=created",
				"loan__Loan_Status__c=Approved, Id=001p0000001oGUrAAM, harMoney_Account_Number__c=C0284207, Name=LAI-00033933, Waived__c=false"));
		
		LoanAccount loanAccount = loanAccountDAO.getByHarmoneyAccountNumber("C0284207");
		assertEquals(LoanAccountStatus.APPROVED,loanAccount.getStatus());
		
		// Change the Loan Account status to Active - Good Standing
		loanAccountProcessor.receiveMessage(getMap(
				"createdDate=2016-01-02T22:47:15.855Z, replayId=207, type=updated",
				"loan__Loan_Status__c=Active - Good Standing, Id=001p0000001oGUrAAM, harMoney_Account_Number__c=C0284207, Name=LAI-00033933, Waived__c=false"));
		loanAccount = loanAccountDAO.getByHarmoneyAccountNumber("C0284207");
		assertEquals(LoanAccountStatus.ACTIVE_GOOD_STANDING,loanAccount.getStatus());
		// No PRRS created
		new CreatedPRRsDTO(protectRealisedRevenueDAO.getAll(),
				0,		// size
				0.00,	// protect waived
				0.00,	// protect realized
				0.00,	// management fee realised
				0.00,	// sales commision fee realised
				true	// all dates are null
				);	
		
		// New Bill is created with satisfied=false
		billProcessor.receiveMessage(getMap(
				"createdDate=2016-02-27T22:10:02.789Z, replayId=400, type=created",
				"loan__Payment_Satisfied__c=false, loan__Loan_Account__c=001p0000001oGUrAAM, CreatedDate=2016-02-27T01:26:22.000Z, loan__Due_Amt__c=353.77, loan__waiver_applied__c=false, loan__Due_Date__c=2016-02-27T00:00:00.000Z, Id=a4np0000000000WAAQ, loan__Transaction_Date__c=2016-02-27T00:00:00.000Z, Name=PCN-0000041405"));
		// 3 PRRs but since this was not a satisfied Bill the ProtectRealised is not yet added
		// ManagementFeeRealised should be updated though.
		new CreatedPRRsDTO(protectRealisedRevenueDAO.getAll(),
				3,		// size
				0.00,	// protect waived
				0.00,	// protect realized
				0.72,	// management fee realised
				0.52,	// sales commision fee realised
				true	// all dates are null
				);	

		// Update the bill with satisfied=true waiver=true
		billProcessor.receiveMessage(getMap(
				"createdDate=2016-02-27T22:10:02.789Z, replayId=400, type=created",
				"loan__Payment_Satisfied__c=true, loan__Loan_Account__c=001p0000001oGUrAAM, CreatedDate=2016-02-27T01:26:22.000Z, loan__Due_Amt__c=353.77, loan__waiver_applied__c=true, loan__Due_Date__c=2016-02-27T00:00:00.000Z, Id=a4np0000000000WAAQ, loan__Transaction_Date__c=2016-02-27T00:00:00.000Z, Name=PCN-0000041405"));
		// Satisfying the Bill updates its PRR with a ProtectRealised figure, the other Management Fee
		// remains unchanged.
		new CreatedPRRsDTO(protectRealisedRevenueDAO.getAll(),
				3,		// size
				2.27,	// protect waived
				2.27,	// protect realized
				0.72,	// management fee realised
				0.52,	// sales commision fee realised
				true	// all dates are null
				);	

		// Change the Loan Account status to Closed-Obligations met
		loanAccountProcessor.receiveMessage(getMap(
				"createdDate=2016-04-27T22:47:15.855Z, replayId=207, type=updated",
				"loan__Loan_Status__c=Closed - Obligations met, Id=001p0000001oGUrAAM, harMoney_Account_Number__c=C0284207, Name=LAI-00033933, Waived__c=false"));
		// By this time there should be PRR records for this loan and the loan itself is closed.
		loanAccount = loanAccountDAO.getByHarmoneyAccountNumber("C0284207");
		assertEquals(LoanAccountStatus.CLOSED_OBLIGATIONS_MET,loanAccount.getStatus());
		// Closing the loan creates another PRR which is the sum of the remaining amortisation
		new CreatedPRRsDTO(protectRealisedRevenueDAO.getAll(),
				6,		// size
				2.27,	// protect waived
				6.82,	// protect realized
				2.17,	// management fee realised
				1.55,	// sales commision fee realised
				false	// all dates are null
				);	
	}
	
	/**
	 * Complete the LoanAccount with waiver status causes the last PRR items to be waivered.
	 */
	@Test
	public void testWaiverLoanAccount() {
		
		List<ProtectRealisedRevenue> createdPRRs = protectRealisedRevenueDAO.getAll();
		assertEquals(0,createdPRRs.size());
		// Loan Account is created initially with status Approved.
		// Does very little but it does create the IMS loan account.
		loanAccountProcessor.receiveMessage(getMap(
				"createdDate=2016-01-01T22:47:15.855Z, replayId=207, type=created",
				"loan__Loan_Status__c=Approved, Id=001p0000001oGUrAAM, harMoney_Account_Number__c=C0284207, Name=LAI-00033933, Waived__c=false"));
		
		LoanAccount loanAccount = loanAccountDAO.getByHarmoneyAccountNumber("C0284207");
		assertEquals(LoanAccountStatus.APPROVED,loanAccount.getStatus());
		
		// Change the Loan Account status to Active - Good Standing
		loanAccountProcessor.receiveMessage(getMap(
				"createdDate=2016-01-02T22:47:15.855Z, replayId=207, type=updated",
				"loan__Loan_Status__c=Active - Good Standing, Id=001p0000001oGUrAAM, harMoney_Account_Number__c=C0284207, Name=LAI-00033933, Waived__c=false"));
		loanAccount = loanAccountDAO.getByHarmoneyAccountNumber("C0284207");
		assertEquals(LoanAccountStatus.ACTIVE_GOOD_STANDING,loanAccount.getStatus());
		// No PRRS created
		new CreatedPRRsDTO(protectRealisedRevenueDAO.getAll(),
				0,		// size
				0.00,	// protect waived
				0.00,	// protect realized
				0.00,	// management fee realised
				0.00,	// sales commision fee realised
				true	// all dates are null
				);	
		
		// New Bill is created with satisfied=false
		billProcessor.receiveMessage(getMap(
				"createdDate=2016-02-27T22:10:02.789Z, replayId=400, type=created",
				"loan__Payment_Satisfied__c=false, loan__Loan_Account__c=001p0000001oGUrAAM, CreatedDate=2016-02-27T01:26:22.000Z, loan__Due_Amt__c=353.77, loan__waiver_applied__c=false, loan__Due_Date__c=2016-02-27T00:00:00.000Z, Id=a4np0000000000WAAQ, loan__Transaction_Date__c=2016-02-27T00:00:00.000Z, Name=PCN-0000041405"));
		// 3 PRRs but since this was not a satisfied Bill the ProtectRealised is not yet added
		// ManagementFeeRealised should be updated though.
		new CreatedPRRsDTO(protectRealisedRevenueDAO.getAll(),
				3,		// size
				0.00,	// protect waived
				0.00,	// protect realized
				0.72,	// management fee realised
				0.52,	// sales commision fee realised
				true	// all dates are null
				);	

		// Update the bill with satisfied=true
		billProcessor.receiveMessage(getMap(
				"createdDate=2016-02-27T22:10:02.789Z, replayId=400, type=created",
				"loan__Payment_Satisfied__c=true, loan__Loan_Account__c=001p0000001oGUrAAM, CreatedDate=2016-02-27T01:26:22.000Z, loan__Due_Amt__c=353.77, loan__waiver_applied__c=false, loan__Due_Date__c=2016-02-27T00:00:00.000Z, Id=a4np0000000000WAAQ, loan__Transaction_Date__c=2016-02-27T00:00:00.000Z, Name=PCN-0000041405"));
		// Satisfying the Bill updates its PRR with a ProtectRealised figure, the other Management Fee
		// remains unchanged.
		new CreatedPRRsDTO(protectRealisedRevenueDAO.getAll(),
				3,		// size
				0.00,	// protect waived
				2.27,	// protect realized
				0.72,	// management fee realised
				0.52,	// sales commision fee realised
				false	// all dates are null
				);	

		// Change the Loan Account status to Closed-Obligations met and waived=true
		loanAccountProcessor.receiveMessage(getMap(
				"createdDate=2016-04-27T22:47:15.855Z, replayId=207, type=updated",
				"loan__Loan_Status__c=Closed - Obligations met, Id=001p0000001oGUrAAM, harMoney_Account_Number__c=C0284207, Name=LAI-00033933, Waived__c=true"));
		// By this time there should be PRR records for this loan and the loan itself is closed.
		loanAccount = loanAccountDAO.getByHarmoneyAccountNumber("C0284207");
		assertEquals(LoanAccountStatus.CLOSED_OBLIGATIONS_MET,loanAccount.getStatus());
		// Closing the loan creates another PRR which is the sum of the remaining amortisation
		new CreatedPRRsDTO(protectRealisedRevenueDAO.getAll(),
				6,		// size
				4.55,	// protect waived
				6.82,	// protect realized
				2.17,	// management fee realised
				1.55,	// sales commision fee realised
				false	// all dates are null
				);	
	}
	
	/**
	 * Move a satisfied bill to unsatisfied
	 */
	@Test
	public void testUnsatisfBill() {
		
		List<ProtectRealisedRevenue> createdPRRs = protectRealisedRevenueDAO.getAll();
		assertEquals(0,createdPRRs.size());
		// Loan Account is created initially with status Approved.
		// Does very little but it does create the IMS loan account.
		loanAccountProcessor.receiveMessage(getMap(
				"createdDate=2016-01-01T22:47:15.855Z, replayId=207, type=created",
				"loan__Loan_Status__c=Approved, Id=001p0000001oGUrAAM, harMoney_Account_Number__c=C0284207, Name=LAI-00033933, Waived__c=false"));
		
		LoanAccount loanAccount = loanAccountDAO.getByHarmoneyAccountNumber("C0284207");
		assertEquals(LoanAccountStatus.APPROVED,loanAccount.getStatus());
		
		// Change the Loan Account status to Active - Good Standing
		loanAccountProcessor.receiveMessage(getMap(
				"createdDate=2016-01-02T22:47:15.855Z, replayId=207, type=updated",
				"loan__Loan_Status__c=Active - Good Standing, Id=001p0000001oGUrAAM, harMoney_Account_Number__c=C0284207, Name=LAI-00033933, Waived__c=false"));
		loanAccount = loanAccountDAO.getByHarmoneyAccountNumber("C0284207");
		assertEquals(LoanAccountStatus.ACTIVE_GOOD_STANDING,loanAccount.getStatus());
		// No PRRS created
		new CreatedPRRsDTO(protectRealisedRevenueDAO.getAll(),
				0,		// size
				0.00,	// protect waived
				0.00,	// protect realized
				0.00,	// management fee realised
				0.00,	// sales commision fee realised
				true	// all dates are null
				);	
		
		// New Bill is created with satisfied=false
		billProcessor.receiveMessage(getMap(
				"createdDate=2016-02-27T22:10:02.789Z, replayId=400, type=created",
				"loan__Payment_Satisfied__c=false, loan__Loan_Account__c=001p0000001oGUrAAM, CreatedDate=2016-02-27T01:26:22.000Z, loan__Due_Amt__c=353.77, loan__waiver_applied__c=false, loan__Due_Date__c=2016-02-27T00:00:00.000Z, Id=a4np0000000000WAAQ, loan__Transaction_Date__c=2016-02-27T00:00:00.000Z, Name=PCN-0000041405"));
		// 3 PRRs but since this was not a satisfied Bill the ProtectRealised is not yet added
		// ManagementFeeRealised should be updated though.
		new CreatedPRRsDTO(protectRealisedRevenueDAO.getAll(),
				3,		// size
				0.00,	// protect waived
				0.00,	// protect realized
				0.72,	// management fee realised
				0.52,	// sales commision fee realised
				true	// all dates are null
				);	

		// Update the bill with satisfied=true
		billProcessor.receiveMessage(getMap(
				"createdDate=2016-02-27T22:10:02.789Z, replayId=400, type=created",
				"loan__Payment_Satisfied__c=true, loan__Loan_Account__c=001p0000001oGUrAAM, CreatedDate=2016-02-27T01:26:22.000Z, loan__Due_Amt__c=353.77, loan__waiver_applied__c=false, loan__Due_Date__c=2016-02-27T00:00:00.000Z, Id=a4np0000000000WAAQ, loan__Transaction_Date__c=2016-02-27T00:00:00.000Z, Name=PCN-0000041405"));
		// Satisfying the Bill updates its PRR with a ProtectRealised figure, the other Management Fee
		// remains unchanged.
		new CreatedPRRsDTO(protectRealisedRevenueDAO.getAll(),
				3,		// size
				0.00,	// protect waived
				2.27,	// protect realized
				0.72,	// management fee realised
				0.52,	// sales commision fee realised
				false	// all dates are null
				);	

		// Update the bill with satisfied=false
		billProcessor.receiveMessage(getMap(
				"createdDate=2016-02-27T22:10:02.789Z, replayId=400, type=updated",
				"loan__Payment_Satisfied__c=false, loan__Loan_Account__c=001p0000001oGUrAAM, CreatedDate=2016-02-27T01:26:22.000Z, loan__Due_Amt__c=353.77, loan__waiver_applied__c=false, loan__Due_Date__c=2016-02-27T00:00:00.000Z, Id=a4np0000000000WAAQ, loan__Transaction_Date__c=2016-02-27T00:00:00.000Z, Name=PCN-0000041405"));
		// Unsatisfying the Bill updates its PRR with a zero ProtectRealised figure, the other Management Fee
		// remains unchanged.
		new CreatedPRRsDTO(protectRealisedRevenueDAO.getAll(),
				3,		// size
				0.00,	// protect waived
				0.00,	// protect realized
				0.72,	// management fee realised
				0.52,	// sales commision fee realised
				true	// all dates are null
				);	

		// Change the Loan Account status to Closed-Obligations met and waived=true
		loanAccountProcessor.receiveMessage(getMap(
				"createdDate=2016-04-27T22:47:15.855Z, replayId=207, type=updated",
				"loan__Loan_Status__c=Closed - Obligations met, Id=001p0000001oGUrAAM, harMoney_Account_Number__c=C0284207, Name=LAI-00033933, Waived__c=true"));
		// By this time there should be PRR records for this loan and the loan itself is closed.
		loanAccount = loanAccountDAO.getByHarmoneyAccountNumber("C0284207");
		assertEquals(LoanAccountStatus.CLOSED_OBLIGATIONS_MET,loanAccount.getStatus());
		// Closing the loan creates another PRR which is the sum of the remaining amortisation
		new CreatedPRRsDTO(protectRealisedRevenueDAO.getAll(),
				6,		// size
				4.55,	// protect waived
				4.55,	// protect realized
				2.17,	// management fee realised
				1.55,	// sales commision fee realised
				false	// all dates are null
				);	
	}
	
	private Map<String, Map<String, Object>> getMap(String eventStr, String sobjectStr) {
		Map<String, Map<String, Object>> map = new HashMap<>();
		map.put("event",getMap(eventStr));
		map.put("sobject",getMap(sobjectStr));
		return map;
	}
	private Map<String,Object> getMap(String str) {
		Map<String,Object> ret = new HashMap<>();
		Properties props = new Properties();
		try {
			props.load(new StringReader(str.replace(", ", "\n")));
		} catch (IOException e1) {
			throw new RuntimeException(e1);
		}       
		for (Map.Entry<Object, Object> e : props.entrySet()) {
			ret.put((String)e.getKey(), (String)e.getValue());
		}
		return ret;
	}
//	private BigDecimal sumPRR_ProtectRealised(List<ProtectRealisedRevenue> createdPRRs) {
//		BigDecimal ret = AmortizationScheduleProcessor.BIG_DECIMAL_ZERO_SCALED;
//		for (ProtectRealisedRevenue prr : createdPRRs) {
//			if (prr.getProtectRealised() != null) {
//				ret = ret.add(prr.getProtectRealised());
//			}
//		}
//		return ret;
//	}
//	
//	private BigDecimal sumPRR_ManagementFeeRealised(List<ProtectRealisedRevenue> createdPRRs) {
//		BigDecimal ret = AmortizationScheduleProcessor.BIG_DECIMAL_ZERO_SCALED;
//		for (ProtectRealisedRevenue prr : createdPRRs) {
//			if (prr.getManagementFeeRealised() != null) {
//				ret = ret.add(prr.getManagementFeeRealised());
//			}
//		}
//		return ret;
//	}
//	private BigDecimal sumPRR_SalesCommissionFeeRealised(List<ProtectRealisedRevenue> createdPRRs) {
//		BigDecimal ret = AmortizationScheduleProcessor.BIG_DECIMAL_ZERO_SCALED;
//		for (ProtectRealisedRevenue prr : createdPRRs) {
//			if (prr.getSalesCommissionFeeRealised() != null) {
//				ret = ret.add(prr.getSalesCommissionFeeRealised());
//			}
//		}
//		return ret;
//	}
//	private BigDecimal sumPRR_ProtectWaived(List<ProtectRealisedRevenue> createdPRRs) {
//		BigDecimal ret = AmortizationScheduleProcessor.BIG_DECIMAL_ZERO_SCALED;
//		for (ProtectRealisedRevenue prr : createdPRRs) {
//			if (prr.getProtectWaived() != null) {
//				ret = ret.add(prr.getProtectWaived());
//			}
//		}
//		return ret;
//	}
//	
//	private boolean allNullDates(List<ProtectRealisedRevenue> createdPRRs) {
//		for (ProtectRealisedRevenue prr : createdPRRs) {
//			if (prr.getProtectRealisedDate() != null) {
//				return false;
//			}
//		}
//		return true;
//	}
	
//	protected static BigDecimal makeBigDecimal(double d) {
//		return new BigDecimal(d).setScale(2, RoundingMode.HALF_UP);
//	}
}
