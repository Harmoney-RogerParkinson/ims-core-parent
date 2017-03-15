package com.harmoney.ims.core.database.descriptors;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.harmoney.ims.core.annotations.SalesforceName;

public class ObjectDescriptor {
	
	private static final Logger log = LoggerFactory.getLogger(ObjectDescriptor.class);
	private final Map<String,PropertyHolder> map = new HashMap<>();
	private final Class<?> clazz;
	private Method idMethod;
	private final String tableName;
	
	public ObjectDescriptor(Class<?> clazz) {
		this.clazz = clazz;
		tableName = clazz.getAnnotation(SalesforceName.class).tableName();
	}

	public void put(String name, Method readMethod, Method writeMethod, Column column) {
		map.put(name, new PropertyHolder(name,readMethod, writeMethod, column, clazz));
	}

	protected PropertyHolder get(String key) {
		return map.get(key);
	}
	
	public Collection<PropertyHolder> getAllProperties() {
		return map.values();
	}
	
	public String toString() {
		StringBuilder ret = new StringBuilder();
		ret.append("\nProperty map "+clazz.getName());
		for (PropertyHolder ph: map.values()) {
			ret.append("\n");
			ret.append(ph.toString());
		}
		ret.append("\n");
		return ret.toString();
	}
	public Result unpack(Map<String, Object> sobject, Object o) {
		Result ret = new Result();
		for (PropertyHolder propertyHolder: getAllProperties()) {
			if (propertyHolder.hasJoinColum()) {
				continue;
			}
			String salesforceName = propertyHolder.getSalesforceName();
			int found = 0;
			for (String name: StringUtils.delimitedListToStringArray(salesforceName, ",")) {
				if (sobject.containsKey(name)) {
					found++;
					unpackName(name,sobject,o,ret,propertyHolder);
				}
			}
			if (found == 0) {
				log.error(ret.error("property not found in sobject: {} fields(s): {}",tableName,salesforceName));
			}
		}
		return ret;
	}
	private boolean unpackName(String salesforceName,Map<String, Object> sobject, Object o,Result ret,PropertyHolder propertyHolder) {
		try {
			Object value = sobject.get(salesforceName);
			if (value == null) {
//				log.warn(ret.warn("property {} was null",salesforceName));
				return false;
			}
//			if (salesforceName.equals("Id")) {
//				log.debug("SF Record id={}",value);
//			}
//			if (salesforceName.equals("Protect_Realised__c")) {
//				log.debug("Protect_Realised__c={}",value);
//			}
//			if (salesforceName.equals("Management_Fee_Realised__c")) {
//				log.debug("Management_Fee_Realised__c={}",value);
//			}
//			if (salesforceName.equals("Sales_Commission_Fee_Realised__c")) {
//				log.debug("Sales_Commission_Fee_Realised__c={}",value);
//			}
			Class<?> columnType = propertyHolder.getColumnType();
			if (value instanceof Double) {
				//convert double to bigdecimal
				value = new BigDecimal((Double)value);
				int scale = propertyHolder.getColumn().scale();
				value = ((BigDecimal)value).setScale(scale,BigDecimal.ROUND_HALF_DOWN);
			} else if (columnType.equals(BigDecimal.class)) {
				// We want a BigDecimal output but input was clearly not a Double
				// Assume it is a string.
				int scale = propertyHolder.getColumn().scale();
				value = new BigDecimal((String)value).setScale(scale,BigDecimal.ROUND_HALF_DOWN);
			} else if (columnType.equals(Date.class)) {
				// Dates arrive as strings which we convert to java.sql.Date
				// but first remove the time component
				String d = ((String)value).substring(0, 10);
				value = java.sql.Date.valueOf(d);
			} else if (columnType.equals(Timestamp.class)) {
				// Dates arrive as strings which we convert to java.sql.Date
				// but first remove the time component
				String d = ((String)value).substring(0, 22);
				value = Timestamp.valueOf(LocalDateTime.parse(d));
			} else if (columnType.isEnum()) {
				value = propertyHolder.valueOf((String) value);
			}
			Method writeMethod = propertyHolder.getWriteMethod();
			try {
				writeMethod.invoke(o, new Object[] { value });
			} catch (Exception e) {
				log.error("name: {} value: {} {}",propertyHolder.getName(),value,e.getMessage());
//				e.printStackTrace();
			}
		} catch (Exception e) {
			log.error(ret.error("Failed to unpack to field: {}.{} {}", clazz,salesforceName,e.getMessage()));
			e.printStackTrace();
			return false;
		}
		return true;
	}
	public void negate(Object target) {
		for (PropertyHolder propertyHolder: map.values()) {
			propertyHolder.negate(target);
		}
	}
	public void zero(Object target) {
		for (PropertyHolder propertyHolder: map.values()) {
			propertyHolder.zero(target);
		}
	}
	public void accumulate(Object source, Object totals) {
		Assert.isTrue(source.getClass().equals(totals.getClass()), "Accumulating objects must be the same class");
		for (PropertyHolder propertyHolder: map.values()) {
			propertyHolder.accumulate(source,totals);
		}
	}
	public void copy(Object source, Object target) {
		Assert.isTrue(source.getClass().equals(target.getClass()), "Accumulating objects must be the same class");
		for (PropertyHolder propertyHolder: map.values()) {
			propertyHolder.copy(source,target);
		}
	}
	public long getId(Object object) {
		Assert.notNull(idMethod,"idMethod must not be null");
		try {
			return (long)idMethod.invoke(object, new Object[]{});
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void setIdMethod(Method idMethod) {
		this.idMethod = idMethod;
	}

	public String getSalesforceTableName() {
		return tableName;
	}

	public List<String> getSalesForceFields() {
		List<String> ret = new ArrayList<>();
		for (PropertyHolder propertyHolder: getAllProperties()) {
			ret.add(propertyHolder.getSalesforceName());
		}
		return ret;
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
