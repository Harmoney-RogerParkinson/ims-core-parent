package com.harmoney.ims.core.queuehandler.unpacker;

import java.lang.reflect.Method;

import javax.persistence.Column;

import org.springframework.util.Assert;

public class PropertyHolder {

	private Column column;
	private Method writeMethod;
	private Class<?> columnType;

	protected PropertyHolder(Method writeMethod, Column column, Class<?> clazz) {
		this.writeMethod = writeMethod;
		this.column = column;
		Class<?> parameterTypes[] = writeMethod.getParameterTypes();
		Assert.isTrue(parameterTypes.length == 1, "setter for property must have exactly one parameter: "+clazz.getName()+"."+writeMethod.getName());
		this.columnType = parameterTypes[0];
	}

	protected Column getColumn() {
		return column;
	}

	protected Method getWriteMethod() {
		return writeMethod;
	}

	protected Object getColumnType() {
		return columnType;
	}

}
