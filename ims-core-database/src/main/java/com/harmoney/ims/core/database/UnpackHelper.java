/**
 * 
 */
package com.harmoney.ims.core.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.harmoney.ims.core.database.descriptors.ObjectDescriptor;
import com.harmoney.ims.core.database.descriptors.Result;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.bind.XmlObject;

/**
 * @author Roger Parkinson
 *
 */
@Component
public class UnpackHelper {

	private static final Logger log = LoggerFactory.getLogger(UnpackHelper.class);
	/**
	 * These fields are in the Transaction object but they are not always supplied by Account and Investment Order
	 * So we use this to suppress unnecessary error messages.
	 * Except we don't really need it now that we aren't inheriting fields we don't want. 
	 */
	private static final String IGNORE_NAMES = "";//"Reverse_Rejected_Date__c,Account_ID__c,harMoney_Account_Number__c,loan__Account__c,CreatedDate";

	public Result unpack(SObject sobject, Object target, ObjectDescriptor objectDescriptor) {
		Map<String, Object> fieldMap = new HashMap<>();
		List<String> salesforceFields = objectDescriptor.getSalesForceFields();
		for (String fieldName : salesforceFields) {
			String[] fieldNames = StringUtils.delimitedListToStringArray(fieldName, ",");
			if (fieldNames.length > 1) {
				fieldNames[0].toString();
			}
			for (String name: fieldNames) {
				String fieldValue;
				try {
					fieldValue = extractValueFromSObject(sobject,name);
				} catch (Exception e) {
					if (!IGNORE_NAMES.contains(name)) {
						log.warn(e.getMessage());
						List<String> allFields = allFields(sobject);
						log.warn("Current field list: {}",allFields.toString());
					}
					continue;
				}
				fieldMap.put(name, fieldValue);
			}
		}
		if (!fieldMap.containsKey("Reverse_Rejected_Date__c")) {
			fieldMap.put("Reverse_Rejected_Date__c", null);
		}
		if (!fieldMap.containsKey("CreatedDate")) {
			fieldMap.put("CreatedDate", null);
		}
		return objectDescriptor.unpack(fieldMap, target);
	}
	private String extractValueFromSObject(SObject sobject, String name) {
		if (name.indexOf('.') == -1) {
			if (sobject.getChild(name) == null) {
				throw new RuntimeException("No field ["+name+"] found in SObject");
			}
			return (String)sobject.getField(name);
		}
		String split[] = StringUtils.split(name, ".");
		SObject f = (SObject)sobject.getField(split[0]);
		if (f == null) {
			return null;
		}
		XmlObject xmlObject = f.getChild(split[1]);
		if (xmlObject == null) {
			throw new RuntimeException("No field ["+split[1]+"] found in SObject");
		}
		return (String)xmlObject.getValue();
	}
	public List<String> allFields(SObject sobject) {
		List<String> ret = new ArrayList<>();
		Iterator<XmlObject> itChildren = sobject.getChildren();
		while (itChildren.hasNext()) {
			XmlObject child = itChildren.next();
			if (child.hasChildren()) {
				ret.addAll(allFields(child));
			} else {
				ret.add(child.getName().getLocalPart());
			}
		}
		return ret;
	}
	public List<String> allFields(XmlObject sobject) {
		List<String> ret = new ArrayList<>();
		Iterator<XmlObject> itChildren = sobject.getChildren();
		while (itChildren.hasNext()) {
			XmlObject child = itChildren.next();
			if (child.hasChildren()) {
				ret.addAll(allFields(child));
			} else {
				ret.add(child.getName().getLocalPart());
			}
		}
		return ret;
	}
}
