package com.harmoney.ims.core.queuehandler.unpacker;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.persistence.Column;

import org.apache.commons.beanutils.PropertyUtilsBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.util.Assert;

import com.harmoney.ims.core.annotations.SalesforceName;
import com.harmoney.ims.core.instances.TxnCode;

/**
 * Stores the classes that the unpacker unpacks to along with their setter methods
 * When called on to unpack an object it finds the correct list of setters for the
 * current object and operates them.
 * 
 * @author Roger Parkinson
 *
 */
@Component
public class Unpacker {

	private static final Logger log = LoggerFactory.getLogger(Unpacker.class);

	@Value("#{'${com.harmoney.ims.core.queuehandler.Unpacker.classes:com.harmoney.ims.core.instances.InvestorLoanTransaction,com.harmoney.ims.core.instances.InvestorFundTransaction}'.split(',')}")
	private List<String> classNames = new ArrayList<String>();

	private Map<Class<?>, PropertyMap> methodMaps = new HashMap<>();

	public Unpacker() {

	}

	/**
	 * Unpack the values in the map into the fields in the given object.
	 * 
	 * @param message
	 * @param o (the target object)
	 * @return the target object
	 */
	public Result unpack(Map<String, Map<String, Object>> message, Object o) {
		Result ret = new Result();
		Class<?> clazz = o.getClass();
		PropertyMap propertyMap = methodMaps.get(clazz);
		if (propertyMap == null) {
			log.error(ret.error("Trying to unpack to an object we don't know about: {}", clazz));
			return ret;
		}
		Map<String, Object> sobject = message.get("sobject");
		Assert.notNull(sobject,"sobject is null");
		for (PropertyHolder propertyHolder: propertyMap.getAllProperties()) {
			String salesforceName = propertyHolder.getSalesforceName();
			if (!sobject.containsKey(salesforceName)) {
				log.error(ret.error("property not found in sobject: {}",salesforceName));
				continue;
			}
			try {
				Object value = sobject.get(salesforceName);
				if (value == null) {
					log.warn(ret.warn("property {} was null",salesforceName));
					continue;
				}
				Class<?> columnType = propertyHolder.getColumnType();
				if (value instanceof Double) {
					//convert double to bigdecimal
					value = new BigDecimal((Double)value);
					int scale = propertyHolder.getColumn().scale();
					((BigDecimal)value).setScale(scale,BigDecimal.ROUND_HALF_DOWN);
				} else if (columnType.equals(Date.class)) {
					// Dates arrive as strings which we convert to java.sql.Date
					// but first remove the time component
					String d = ((String)value).substring(0, 10);
					value = java.sql.Date.valueOf(d);
				} else if (columnType.equals(LocalDate.class)) {
					// Dates arrive as strings which we convert to java.sql.Date
					// but first remove the time component
					String d = ((String)value).substring(0, 10);
					value = LocalDate.parse(d);
				} else if (columnType.isEnum()) {
					value = propertyHolder.valueOf((String) value);
				}
				Method writeMethod = propertyHolder.getWriteMethod();
				writeMethod.invoke(o, new Object[] { value });
			} catch (Exception e) {
				log.error(ret.error("Failed to unpack to field: {}.{} {}", clazz,propertyHolder.getName(),e.getMessage()));
				continue;
			}
		}
		return ret;
	}

	@PostConstruct
	public void init() {
		PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean();
		for (String className : classNames) {
			Class<?> clazz;
			try {
				clazz = Class.forName(className);
			} catch (ClassNotFoundException e) {
				log.warn("Failed to find class: {} ignoring...", className);
				continue;
			}
			PropertyDescriptor[] descriptors = propertyUtilsBean
					.getPropertyDescriptors(clazz);
			PropertyMap propertyMap = new PropertyMap(clazz);
			for (PropertyDescriptor descriptor : descriptors) {
				String name = descriptor.getName();
				Method writeMethod = descriptor.getWriteMethod();
				if (writeMethod == null) {
					continue;
				}
				Method readMethod = descriptor.getReadMethod();
				if (readMethod == null) {
					continue;
				}
				SalesforceName salesforceName = readMethod.getAnnotation(SalesforceName.class);
				if (salesforceName == null) {
					continue;
				}
				Column column = readMethod.getAnnotation(Column.class);
				propertyMap.put(name, readMethod, writeMethod,column);
			}
			methodMaps.put(clazz, propertyMap);
			log.debug("{}",propertyMap);
		}
	}
	
	public static String fixVariableFormat(String vname) {
		vname = vname.replaceAll("^loan__", "");
		vname = vname.replaceAll("__c$", "");
		vname = vname.replaceAll("__", "_");
		StringBuilder ret = new StringBuilder();
		String words[] = StringUtils.tokenizeToStringArray(vname, "_");
		if (words == null) {
			ret.append(figureCase(vname,true));
		} else {
			int count = 0;
			for (String thisWord : words) {
				if (!StringUtils.isEmpty(thisWord)) {
					ret.append(figureCase(thisWord,count++ < 1));
				}
			}
		}
		return ret.toString();
	}
	private static String figureCase(String r, boolean upperLower) {
		if (Character.isAlphabetic(r.charAt(1)) && Character.isLowerCase(r.charAt(1))) {
			if (upperLower) {
				r = Character.toLowerCase(r.charAt(0)) + r.substring(1);
			} else {
				r = Character.toUpperCase(r.charAt(0)) + r.substring(1);
			}
		}
		return r;
	}
}
