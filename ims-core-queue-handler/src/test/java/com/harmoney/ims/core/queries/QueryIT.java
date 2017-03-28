package com.harmoney.ims.core.queries;

import java.io.FileWriter;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.harmoney.ims.core.database.ConfiguredDatabaseParameters;
import com.harmoney.ims.core.database.DatabaseSpringConfig;
import com.harmoney.ims.core.partner.PartnerConnectionSpringConfig;
import com.harmoney.ims.core.queueprocessor.PartnerConnectionWrapper;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.bind.XmlObject;

@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource("/QueryTest.properties")
@ContextConfiguration(classes = { QuerySpringConfig.class, PartnerConnectionSpringConfig.class,DatabaseSpringConfig.class})
@ActiveProfiles("queue-handler-prod")
public class QueryIT {

	private static final Logger log = LoggerFactory
			.getLogger(QueryIT.class);

	@Autowired
	ConfigurableApplicationContext context;
	@Autowired AccountQuery accountquery;
	@Autowired InvestmentOrderQuery investmentOrderquery;
	@Autowired ConfiguredDatabaseParameters configuredParameters;
	@Autowired private PartnerConnectionWrapper partnerConnection;

	@Test
	public void testAccountSummaryQuery() throws Exception {
		
		log.info("Database hbm2ddlauto={} database dialect {}",configuredParameters.getHbm2ddlAuto(),configuredParameters.getDialect());
		accountquery.doQuery();
	}

	@Test
	public void testInvestmentOrderQuery() throws Exception {
		
		log.info("Database hbm2ddlauto={} database dialect {}",configuredParameters.getHbm2ddlAuto(),configuredParameters.getDialect());
		investmentOrderquery.doQuery();
	}
	@Test @Ignore
	public void testJoinQuery() throws Exception {
		String queryString = "SELECT loan__Loan_Account__c FROM loan__Repayment_Schedule__c where Protect_Realised__c > 0 group by loan__Loan_Account__c";

		SObject[] records = partnerConnection.query(queryString);
		log.info(" size {}",records.length);
		for (SObject sobject: records) {
			String loanAccountId = (String) sobject.getField("loan__Loan_Account__c");
			SObject[] records1 = partnerConnection.query("Select Id,Name,CreatedDate,loan__Account__c,loan__Loan__c From   loan__Investor_Loan__c WHERE loan__Account__c = '"+loanAccountId+"'");
			log.info("{} size {}",loanAccountId,records1.length);
			for (SObject record1: records1) {
				log.info(">>>>{}",record1.getField("loan__Account__c"));
			}
		}
	}
	
	@Test @Ignore
	public void testExtractQuery() throws Exception {
		String queryString = "Select Id ,Name ,CreatedDate  ,loan__Account__c ,loan__Loan__c  ,loan__Investment_Amount__c ,HM_Investment_Amount__c  ,Protect_Investment_Amount__c ,loan__Charged_Off_Principal__c ,loan__Charged_Off_Date__c  ,loan__Loan_Status__c ,loan__Share__c	 ,Payment_Protect_Management_Fees__c ,Payment_Protect_Sales_Commission_Fees__c ,Payment_Protect_Fee__c ,Payment_Protect_Rebated_Amount__c  ,HM_Rollup_Outstanding_Principal__c From loan__Investor_Loan__c where Protect_Investment_Amount__c > 0 and CreatedDate > 2016-01-01T03:34:33.000Z and loan__Account__c ='001p0000001oGUrAAM' order by CreatedDate";

		SObject[] records = partnerConnection.query(queryString);
		log.info(" size {}", records.length);
		Document document = DocumentHelper.createDocument();
		Element root = document.addElement("queryResult");
		for (SObject sobject : records) {
			Element record = root.addElement("records");
			Iterator<XmlObject> it = sobject.getChildren();
			while (it.hasNext()) {
				XmlObject o = it.next();
				Object v = o.getValue();
				Element field = record.addElement(o.getName().getLocalPart());
				if (v != null) {
					field.setText(v.toString());
				}
			}
		}
		XMLWriter writer = new XMLWriter(new FileWriter("output.xml"));
		writer.write(document);
		writer.close();

		// Pretty print the document to System.out
		OutputFormat format = OutputFormat.createPrettyPrint();
		writer = new XMLWriter(System.out, format);
		writer.write(document);
	}	
	@Test @Ignore
	public void testExtractQuery2() throws Exception {
		{
			String fieldList = "Id,Name,loan__Loan_Account__c,loan__Due_Date__c,Protect_Realised__c,Sales_Commission_Realised__c,Management_Fee_Realised__c";
			String queryString = "SELECT "+fieldList+"  FROM loan__Repayment_Schedule__c where loan__Loan_Account__c = 'a4Xp00000008OI5'";
			inserts(fieldList,"loan__Repayment_Schedule__c",queryString);
		}
		{
			String fieldList = "Id,Name,loan__Account__c,loan__Share__c,loan__Loan_Status__c";
			String queryString = "SELECT "+fieldList+"  FROM loan__Investor_loan__c where loan__Account__c = 'a4Xp00000008OI5'";
			inserts(fieldList,"loan__Investor_loan__c",queryString);
		}
	}
	
	
	private void inserts(String fieldList, String tableName, String queryString) throws Exception {
		SObject[] records = partnerConnection.query(queryString);
		log.info(" size {}", records.length);
		StringBuilder sb = new StringBuilder();
		
		for (SObject sobject : records) {
			StringBuilder sb1 = new StringBuilder();
			sb1.append("INSERT INTO "+tableName+" ("+fieldList.replaceAll("Name", "Name_")+") values (");
			StringTokenizer st = new StringTokenizer(fieldList,",");
			while (st.hasMoreTokens()) {
				Object f = sobject.getField(st.nextToken());
				if (f instanceof String) {
					sb1.append("\'");
					sb1.append(f);
					sb1.append("\'");
				} else {
					sb1.append(f);
				}
				sb1.append(",");
			}
			sb1.setCharAt(sb1.lastIndexOf(","),')');
			sb1.append(";\n");
			sb.append(sb1);
		}
		log.info("\n{}",sb);

	}

}
