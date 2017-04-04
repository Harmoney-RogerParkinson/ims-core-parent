/**
 * 
 */
package com.harmoney.ims.core.queueprocessor;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.XMLWriter;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.harmoney.ims.core.database.DatabaseSpringConfig;
import com.harmoney.ims.core.queries.AmortizationScheduleQuery;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;

/**
 * @author Roger Parkinson
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { DatabaseSpringConfig.class,AmortizationSpringConfig.class })
@ActiveProfiles("queue-handler-dev")
@TestPropertySource("/H2Test.properties")
public class XMLBuilderTest {
	
	@Autowired PartnerConnectionWrapperMock partnerConnectionWrapperMock;
	@Autowired DataSource db;


	@Test @Ignore
	public void createXML() throws ConnectionException, IOException {
		
		Document document = DocumentHelper.createDocument();
		Element root = document.addElement("root");
		query(root,"SELECT Id,Name, loan__Loan_Account__c,loan__Due_Date__c, Protect_Realised__c, "
				+ "Sales_Commission_Realised__c, Management_Fee_Realised__c "
				+ "FROM loan__Repayment_Schedule__c order by loan__Due_Date__c","loan__Repayment_Schedule__c");
		query(root,"SELECT Id,loan__Account__c,loan__Share__c,loan__Loan_Status__c FROM loan__Investor_Loan__c ","loan__Investor_Loan__c");
		XMLWriter writer = new XMLWriter( new FileWriter( "output.xml" ));
        writer.write( document );
        writer.close();
	}

	private void query(Element root,String queryString, String tableName)
			throws ConnectionException, IOException {
		String[] fieldList = StringUtils.stripAll(StringUtils.split(
				StringUtils.substringBetween(queryString, "SELECT ", " FROM"),
				','));
		Connection connection = null;
		ResultSet rs = null;
		try {
			connection = db.getConnection();
			rs = connection.prepareStatement(
					queryString.replaceAll("Name", "Name_")).executeQuery();
			ResultSetMetaData metaData = rs.getMetaData();
			int columns = metaData.getColumnCount();
			while (rs.next()) {
				Element row = root.addElement(tableName);
				for (int column = 1; column <= columns; column++) {
					Object value = rs.getObject(column);
					Element field = row.addElement(fieldList[column - 1]);
					if (value != null) {
						field.setText(value.toString());
					}
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
		return;
	}
	@Test @Ignore
	public void testXMLQueries() throws ConnectionException {
		@SuppressWarnings("unused")
		SObject[] sobjects = partnerConnectionWrapperMock.query(AmortizationScheduleQuery.SOQL
				+ "WHERE loan__Loan_Account__c = 'whatever' and loan__Due_Date__c >='2020-11-27' order by loan__Due_Date__c");
	}

}
