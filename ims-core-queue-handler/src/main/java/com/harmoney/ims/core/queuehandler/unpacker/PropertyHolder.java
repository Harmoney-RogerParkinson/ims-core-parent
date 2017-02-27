package com.harmoney.ims.core.queuehandler.unpacker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.persistence.Column;

import org.slf4j.helpers.MessageFormatter;
import org.springframework.util.Assert;

import com.harmoney.ims.core.annotations.SalesforceName;

public class PropertyHolder {

	private Column column;
	private String name;
	private Method writeMethod;
	private Method readMethod;
	private Class<?> columnType;
	private String salesforceName;
	private Method enumValueOf;

	protected PropertyHolder(String name, Method readMethod,Method writeMethod, Column column, Class<?> clazz) {
		this.writeMethod = writeMethod;
		this.readMethod = readMethod;
		this.column = column;
		this.name = name;
		Class<?> parameterTypes[] = writeMethod.getParameterTypes();
		Assert.isTrue(parameterTypes.length == 1, "setter for property must have exactly one parameter: "+clazz.getName()+"."+writeMethod.getName());
		this.columnType = parameterTypes[0];
		SalesforceName salesforceName = readMethod.getAnnotation(SalesforceName.class);
		this.salesforceName = salesforceName.fieldName();
		if (columnType.isEnum()) {
			try {
				enumValueOf = columnType.getMethod("valueOf", String.class);
			} catch (NoSuchMethodException e) {
				throw new RuntimeException("Need a 'valueOf' method on enum type "+columnType.getName(),e);
			}
		}
	}

	protected Column getColumn() {
		return column;
	}

	protected Method getWriteMethod() {
		return writeMethod;
	}

	protected Class<?> getColumnType() {
		return columnType;
	}

	protected Method getReadMethod() {
		return readMethod;
	}

	protected String getName() {
		return name;
	}

	protected String getSalesforceName() {
		return salesforceName;
	}
	public String toString() {
		StringBuilder ret = new StringBuilder();
		ret.append("name: "+name);
		ret.append(" salesforceName: "+salesforceName);
		ret.append(" columnType: "+columnType.getName());
		return ret.toString();
	}

	protected Object valueOf(String value) {
		try {
			return enumValueOf.invoke(null, value.replace(' ', '_'));
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

}
