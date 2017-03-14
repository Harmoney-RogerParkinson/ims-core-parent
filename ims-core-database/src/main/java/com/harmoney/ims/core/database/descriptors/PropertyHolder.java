package com.harmoney.ims.core.database.descriptors;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.JoinColumn;

import org.springframework.util.Assert;

import com.harmoney.ims.core.annotations.Negateable;
import com.harmoney.ims.core.annotations.SalesforceName;

public class PropertyHolder {

	private Column column;
	private String name;
	private Method writeMethod;
	private Method readMethod;
	private Class<?> columnType;
	private String salesforceName;
	private Method enumValueOf;
	private boolean joinColumn;
	private boolean negateable;

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
		joinColumn = (readMethod.getAnnotation(JoinColumn.class) != null);
		negateable = (readMethod.getAnnotation(Negateable.class) != null);
		if (negateable && !columnType.equals(BigDecimal.class)) {
			throw new RuntimeException("Column "+name+" is negatable but not BigDecimal");
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
			return enumValueOf.invoke(null, value.replace(' ', '_').replace('-', '_'));
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	protected boolean hasJoinColum() {
		return joinColumn;
	}

	protected void negate(Object target) {
		if (negateable) {
			try {
				BigDecimal bigDecimal = (BigDecimal)readMethod.invoke(target);
				if (bigDecimal != null) {
					writeMethod.invoke(target, bigDecimal.negate().setScale(column.scale(),BigDecimal.ROUND_HALF_DOWN));
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
	public void zero(Object target) {
		if (negateable) {
			try {
				BigDecimal bigDecimal = new BigDecimal(0);
				if (bigDecimal != null) {
					writeMethod.invoke(target, bigDecimal.setScale(column.scale(),BigDecimal.ROUND_HALF_DOWN));
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	protected void accumulate(Object source, Object totals) {
		if (negateable) {
			try {
				BigDecimal bigDecimal = (BigDecimal)readMethod.invoke(source);
				if (bigDecimal != null) {
					BigDecimal bigDecimalTotal = (BigDecimal)readMethod.invoke(totals);
					if (bigDecimalTotal == null) {
						bigDecimalTotal = new BigDecimal(0).setScale(column.scale(),BigDecimal.ROUND_HALF_DOWN);
					}
					bigDecimalTotal = bigDecimalTotal.add(bigDecimal);
					writeMethod.invoke(totals, bigDecimalTotal.setScale(column.scale(),BigDecimal.ROUND_HALF_DOWN));
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		
	}

	public void copy(Object source, Object target) {
		if (negateable) {
			try {
				BigDecimal bigDecimal = (BigDecimal)readMethod.invoke(source);
				if (bigDecimal == null) {
					bigDecimal = new BigDecimal(0).setScale(column.scale(),BigDecimal.ROUND_HALF_DOWN);
				}
				writeMethod.invoke(target, bigDecimal.setScale(column.scale(),BigDecimal.ROUND_HALF_DOWN));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

}
