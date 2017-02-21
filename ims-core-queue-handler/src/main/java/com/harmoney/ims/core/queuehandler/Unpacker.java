package com.harmoney.ims.core.queuehandler;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.beanutils.PropertyUtilsBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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

	Map<Class<?>, Map<String, Method>> methodMaps = new HashMap<>();

	public Unpacker() {

	}

	/**
	 * Unpack the values in the map into the fields in the given object.
	 * 
	 * @param message
	 * @param o (the target object)
	 * @return the target object
	 */
	public Object unpack(Map<String, Map<String, Object>> message, Object o) {
		Class<?> clazz = o.getClass();
		Map<String, Method> methodMap = methodMaps.get(clazz);
		if (methodMap == null) {
			log.error("Trying to unpack to an object we don't know about: {}", clazz);
			return o;
		}
		Map<String, Object> o1 = message.get("sobject");
		for (Map.Entry<String, Object> entry : o1.entrySet()) {
			String fieldName = entry.getKey();
			// TODO: eventually translate the Salesforce field name to a
			// sensible one
			Method method = methodMap.get(fieldName);
			if (method == null) {
				log.error("Trying to unpack to field we don't know about: {}.{}", clazz,fieldName);
				continue;
			}
			try {
				method.invoke(o, new Object[] { entry.getValue() });
			} catch (Exception e) {
				log.error("Failed to unpack to field: {}.{} {}", clazz,fieldName,e.getMessage());
				continue;
			}
		}
		return o;
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
			Map<String, Method> methodMap = new HashMap<String, Method>();
			for (PropertyDescriptor descriptor : descriptors) {
				methodMap
						.put(descriptor.getName(), descriptor.getWriteMethod());
			}
			methodMaps.put(clazz, methodMap);
		}

	}
}
