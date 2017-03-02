/**
 * 
 */
package com.harmoney.ims.core.database.descriptors;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Id;

import org.apache.commons.beanutils.PropertyUtilsBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.harmoney.ims.core.annotations.Negateable;
import com.harmoney.ims.core.annotations.SalesforceName;

/**
 * Builds the cache of reflection information about the given class.
 * Saves us doing reflection all the time. We store all the attributes annotated with
 * Negatable in a list with their getter and setter methods, and the BigDecimal scale.
 * We also store the Id field getter.
 * This is only run at startup.
 * 
 * @author Roger Parkinson
 *
 */
@Component
public class ObjectDescriptorGenerator {

	private static final Logger log = LoggerFactory.getLogger(ObjectDescriptorGenerator.class);
/*
	public ObjectDescriptor build(Class<?> clazz) {
		ObjectDescriptor ret = new ObjectDescriptor(clazz);
		PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean();
		PropertyDescriptor[] descriptors = propertyUtilsBean
				.getPropertyDescriptors(clazz);
		for (PropertyDescriptor descriptor : descriptors) {
			Method readMethod = descriptor.getReadMethod();
			if (readMethod == null) {
				continue;
			}
			if (readMethod.getAnnotation(Id.class) != null) {
				ret.setIdMethod(readMethod);
				continue;
			}
			if (readMethod.getAnnotation(Negateable.class) == null) {
				continue;
			}
			Method writeMethod = descriptor.getWriteMethod();
			if (writeMethod == null) {
				log.error("failed to find write method for {}",descriptor.getName());
				continue;
			}
			Class<?> parameterTypes[] = writeMethod.getParameterTypes();
			Assert.isTrue(parameterTypes.length == 1, "setter for property must have exactly one parameter: "+clazz.getName()+"."+writeMethod.getName());
			if (parameterTypes[0] != BigDecimal.class) {
				log.error("Negatables must be BigDecimal: {}",descriptor.getName());
				continue;
			}
			Column column = readMethod.getAnnotation(Column.class);
			ret.add(new NegatableDescriptor(readMethod, writeMethod, column.scale()));
		}
		return ret;
	}
	*/
	public ObjectDescriptor build(Class<?> clazz) {
		PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean();
		PropertyDescriptor[] descriptors = propertyUtilsBean
				.getPropertyDescriptors(clazz);
		ObjectDescriptor propertyMap = new ObjectDescriptor(clazz);
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
			SalesforceName salesforceName = readMethod
					.getAnnotation(SalesforceName.class);
			if (salesforceName == null) {
				continue;
			}
			if (readMethod.getAnnotation(Id.class) != null) {
				propertyMap.setIdMethod(readMethod);
			}
			Column column = readMethod.getAnnotation(Column.class);
			propertyMap.put(name, readMethod, writeMethod, column);
		}
		log.debug("{}", propertyMap);
		return propertyMap;
	}
}


