/**
 * 
 */
package com.harmoney.ims.core.queueprocessor;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
public class AmortizationProcessorTest {
	
	@Autowired BillProcessor billProcessor;
	@Autowired LoanAccountProcessor loanAccountProcessor;
	@Autowired ProtectRealisedRevenueDAO protectRealisedRevenueDAO;
	@Autowired private LoanAccountDAO loanAccountDAO;
//	@Autowired PartnerConnectionWrapperMock partnerConnectionWrapperMock;
//	@Autowired DataSource db;


//	@Test
//	public void createXML() throws ConnectionException, IOException {
//		
//		Document document = DocumentHelper.createDocument();
//		Element root = document.addElement("root");
//		query(root,"SELECT Id,Name, loan__Loan_Account__c,loan__Due_Date__c, Protect_Realised__c, "
//				+ "Sales_Commission_Realised__c, Management_Fee_Realised__c "
//				+ "FROM loan__Repayment_Schedule__c order by loan__Due_Date__c","loan__Repayment_Schedule__c");
//		query(root,"SELECT Id,loan__Account__c,loan__Share__c,loan__Loan_Status__c FROM loan__Investor_Loan__c ","loan__Investor_Loan__c");
//		OutputFormat format = OutputFormat.createPrettyPrint();
//		XMLWriter writer = new XMLWriter( new FileWriter( "output.xml" ));
//        writer.write( document );
//        writer.close();
//	}

//	private void query(Element root,String queryString, String tableName)
//			throws ConnectionException, IOException {
//		String[] fieldList = StringUtils.stripAll(StringUtils.split(
//				StringUtils.substringBetween(queryString, "SELECT ", " FROM"),
//				','));
//		Connection connection = null;
//		ResultSet rs = null;
//		try {
//			connection = db.getConnection();
//			rs = connection.prepareStatement(
//					queryString.replaceAll("Name", "Name_")).executeQuery();
//			ResultSetMetaData metaData = rs.getMetaData();
//			int columns = metaData.getColumnCount();
//			while (rs.next()) {
//				Element row = root.addElement(tableName);
//				for (int column = 1; column <= columns; column++) {
//					Object value = rs.getObject(column);
//					Element field = row.addElement(fieldList[column - 1]);
//					if (value != null) {
//						field.setText(value.toString());
//					}
//				}
//			}
//		} catch (SQLException e) {
//			throw new RuntimeException(e);
//		} finally {
//			try {
//				if (rs != null) {
//					rs.close();
//				}
//				if (connection != null) {
//					connection.close();
//				}
//			} catch (SQLException e) {
//				throw new RuntimeException(e);
//			}
//		}
//		return;
//	}
//	@Test
//	public void testXMLQueries() throws ConnectionException {
//		SObject[] sobjects = partnerConnectionWrapperMock.query(AmortizationScheduleQuery.SOQL
//				+ "WHERE loan__Loan_Account__c = 'whatever' and loan__Due_Date__c >='2020-11-27' order by loan__Due_Date__c");
//	}

	/**
	 * Test method for {@link com.harmoney.ims.core.queueprocessor.BillProcessor#receiveMessage(java.util.Map)}.
	 */
	@Test
	public void testReceiveMessage() {
		
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
		List<ProtectRealisedRevenue> createdPRRs = protectRealisedRevenueDAO.getAll();
		// No PRRS created
		assertEquals(0,createdPRRs.size());
		assertEquals(new BigDecimal(0),sumPRR_ProtectRealised(createdPRRs));
		assertEquals(new BigDecimal(0),sumPRR_ManagementFeeRealised(createdPRRs));
		
		// New Bill is created with satisfied=false
		billProcessor.receiveMessage(getMap(
				"createdDate=2016-02-27T22:10:02.789Z, replayId=400, type=created",
				"loan__Payment_Satisfied__c=false, loan__Loan_Account__c=001p0000001oGUrAAM, CreatedDate=2016-02-27T01:26:22.000Z, loan__Due_Amt__c=353.77, loan__waiver_applied__c=false, loan__Due_Date__c=2016-02-27T00:00:00.000Z, Id=a4np0000000000WAAQ, loan__Transaction_Date__c=2016-02-27T00:00:00.000Z, Name=PCN-0000041405"));
		createdPRRs = protectRealisedRevenueDAO.getAll();
		// 3 PRRs but since this was not a satisfied Bill the ProtectRealised is not yet added
		// ManagementFeeRealised should be updated though.
		assertEquals(3,createdPRRs.size());
		assertEquals(new BigDecimal(0),sumPRR_ProtectRealised(createdPRRs));
		assertEquals(new BigDecimal(0.72).setScale(2, RoundingMode.HALF_UP),sumPRR_ManagementFeeRealised(createdPRRs));

		// Update the bill with satisfied=true
		billProcessor.receiveMessage(getMap(
				"createdDate=2016-02-27T22:10:02.789Z, replayId=400, type=created",
				"loan__Payment_Satisfied__c=true, loan__Loan_Account__c=001p0000001oGUrAAM, CreatedDate=2016-02-27T01:26:22.000Z, loan__Due_Amt__c=353.77, loan__waiver_applied__c=false, loan__Due_Date__c=2016-02-27T00:00:00.000Z, Id=a4np0000000000WAAQ, loan__Transaction_Date__c=2016-02-27T00:00:00.000Z, Name=PCN-0000041405"));
		createdPRRs = protectRealisedRevenueDAO.getAll();
		// Satisfying the Bill updates its PRR with a ProtectRealised figure, the other Management Fee
		// remains unchanged.
		assertEquals(3,createdPRRs.size());
		assertEquals(new BigDecimal(2.27).setScale(2, RoundingMode.HALF_UP),sumPRR_ProtectRealised(createdPRRs));
		assertEquals(new BigDecimal(0.72).setScale(2, RoundingMode.HALF_UP),sumPRR_ManagementFeeRealised(createdPRRs));

		// Change the Loan Account status to Closed-Obligations met
		loanAccountProcessor.receiveMessage(getMap(
				"createdDate=2016-04-27T22:47:15.855Z, replayId=207, type=updated",
				"loan__Loan_Status__c=Closed - Obligations met, Id=001p0000001oGUrAAM, harMoney_Account_Number__c=C0284207, Name=LAI-00033933, Waived__c=false"));
		// By this time there should be PRR records for this loan and the loan itself is closed.
		loanAccount = loanAccountDAO.getByHarmoneyAccountNumber("C0284207");
		assertEquals(LoanAccountStatus.CLOSED_OBLIGATIONS_MET,loanAccount.getStatus());
		createdPRRs = protectRealisedRevenueDAO.getAll();
		// Closing the loan creates another PRR which is the sum of the remaining amortisation
		assertEquals(6,createdPRRs.size());
		assertEquals(new BigDecimal(9.09).setScale(2, RoundingMode.HALF_UP),sumPRR_ProtectRealised(createdPRRs));
		assertEquals(new BigDecimal(2.89).setScale(2, RoundingMode.HALF_UP),sumPRR_ManagementFeeRealised(createdPRRs));
	}
	
	private BigDecimal sumPRR_ProtectRealised(List<ProtectRealisedRevenue> createdPRRs) {
		BigDecimal ret = new BigDecimal(0);
		for (ProtectRealisedRevenue prr : createdPRRs) {
			if (prr.getProtectRealised() != null) {
				ret = ret.add(prr.getProtectRealised());
			}
		}
		return ret;
	}
	
	private BigDecimal sumPRR_ManagementFeeRealised(List<ProtectRealisedRevenue> createdPRRs) {
		BigDecimal ret = new BigDecimal(0);
		for (ProtectRealisedRevenue prr : createdPRRs) {
			if (prr.getManagementFeeRealised() != null) {
				ret = ret.add(prr.getManagementFeeRealised());
			}
		}
		return ret;
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
}
