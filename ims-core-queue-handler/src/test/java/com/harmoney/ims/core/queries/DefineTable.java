package com.harmoney.ims.core.queries;

import java.util.Iterator;

import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.bind.XmlObject;

/**
 * Used to extract a rough table definition from a query for the XSD. Not used in production.
 * 
 * @author Roger Parkinson
 *
 */
public class DefineTable {
	
	public static String getDefinition(SObject sobject) {
		StringBuilder sb = new StringBuilder();
		Iterator<XmlObject> it = sobject.getChildren();
		while (it.hasNext()) {
			XmlObject xmlObject = it.next();
			String n = xmlObject.getName().getLocalPart();
			if (n.equals("type")) {
				continue;
			}
			sb.append("<element name=\"");
			sb.append(n);
			sb.append("\" type=\"tns:currency\">\n");
			sb.append("<annotation>\n<appinfo>\n<annox:annotate><ims:SalesforceName fieldName=\"");
			sb.append(n);
			sb.append("\" />\n");
			sb.append("<ims:Negateable />\n");
			sb.append("</annox:annotate>\n");
			sb.append("</appinfo>\n");
			sb.append("</annotation>\n");
			sb.append("</element>\n");
		}
		return sb.toString();
	}

}
