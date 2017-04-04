package com.harmoney.ims.core.queueprocessor;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.harmoney.ims.core.partner.PartnerConnectionWrapper;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;

/**
 * Mocks the Salesforce interface using an XML file and some XPath queries on it.
 * The queries are very limited so we don't have to parse the SQL very much.
 * 
 * @author Roger Parkinson
 *
 */
@Component
@Profile("queue-handler-dev")
public class PartnerConnectionWrapperMock implements PartnerConnectionWrapper {
	
	private Document salesforceMock; 

	public PartnerConnectionWrapperMock() {
		
	}
	
	/* (non-Javadoc)
	 * @see com.harmoney.ims.core.queries.PartnerConnectionWrapper#query(java.lang.String)
	 */
	@Override
	public SObject[] query(String queryString) throws ConnectionException {
		String dueDate = null;
		if (queryString.contains("loan__Investor_Loan__c")) {
			@SuppressWarnings("unchecked")
			List<Element> elements = salesforceMock.getRootElement().elements("loan__Investor_Loan__c");
			return makeSObjects(elements);
		}
		dueDate = extractParam(queryString,"loan__Due_Date__c >= ");
		if (dueDate != null) {
			@SuppressWarnings("unchecked")
			List<Element> elements = salesforceMock.getRootElement().selectNodes("//root/loan__Repayment_Schedule__c[translate(loan__Due_Date__c, \"-:T\", \"\") >= translate('"+dueDate+"', \"-:T\", \"\")]");
			return makeSObjects(elements);

		}
		dueDate = extractParam(queryString,"loan__Due_Date__c = ");
		if (dueDate != null) {
			@SuppressWarnings("unchecked")
			List<Element> elements = salesforceMock.getRootElement().selectNodes("//root/loan__Repayment_Schedule__c[loan__Due_Date__c = '"+dueDate+"']");
			return makeSObjects(elements);
		}
		@SuppressWarnings("unchecked")
		List<Element> elements = salesforceMock.getRootElement().elements("loan__Repayment_Schedule__c");
		return makeSObjects(elements);
	}
	private String extractParam(String query, String previous) {
		int i = query.indexOf(previous);
		if (i < 0) {
			return null;
		}
		i = i + previous.length();
		int i1 = query.indexOf('T',i);
		if (i1 < 0) {
			i1 = query.length();
		}
		return query.substring(i,i1);
	}

	@SuppressWarnings("unchecked")
	private SObject[] makeSObjects(List<Element> elements) {
		List<SObject> ret = new ArrayList<>();
		for (Element element: elements) {
			SObject sobject = new SObject();
			ret.add(sobject);
			for (Element field: (List<Element>)element.elements()) {
				if (field.hasContent()) {
					sobject.addField(field.getName(),field.getText());
				} else {
					sobject.addField(field.getName(),null);
				}
			}
		}
		return ret.toArray(new SObject[ret.size()]);
	}

	@PostConstruct
	private void loadQueryResults() {
        try {
			SAXReader reader = new SAXReader();
			InputStream is = this.getClass().getResourceAsStream("salesforcemock.xml");
			salesforceMock = reader.read(is);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void logout() {
		// do nothing
		
	}
	
//	private SObject[] loadQueryResult(String file) throws FileNotFoundException, DocumentException {
//		List<SObject> ret = new ArrayList<>();
//		SAXReader reader = new SAXReader();
//		InputStream is = new FileInputStream(file);
//        Document document = reader.read(is);
//        Element root = document.getRootElement();
//        for ( Iterator i = root.elementIterator( "records" ); i.hasNext(); ) {
//            Element foo = (Element) i.next();
//            SObject sobject = new SObject();
//            ret.add(sobject);
//            for ( Iterator i1 = foo.elementIterator(); i1.hasNext(); ) {
//                Element foo1 = (Element) i1.next();
//                String nil = foo1.attributeValue("nil");
//                if ("true".equals(nil)) {
//                	sobject.addField(foo1.getName(), null);
//                } else {
//                	sobject.addField(foo1.getName(), foo1.getText());
//                }
//            }
//        }
//        return ret.toArray(new SObject[ret.size()]);
//	}
}
