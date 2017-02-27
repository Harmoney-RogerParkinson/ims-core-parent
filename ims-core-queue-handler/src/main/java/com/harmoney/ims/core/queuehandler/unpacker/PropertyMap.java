package com.harmoney.ims.core.queuehandler.unpacker;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;

import org.slf4j.helpers.MessageFormatter;

public class PropertyMap {
	
	private final Map<String,PropertyHolder> map = new HashMap<>();
	private final Class<?> clazz;
	
	protected PropertyMap(Class<?> clazz) {
		this.clazz = clazz;
	}

	protected void put(String name, Method readMethod, Method writeMethod, Column column) {
		map.put(name, new PropertyHolder(name,readMethod, writeMethod, column, clazz));
		
	}

	protected PropertyHolder get(String key) {
		return map.get(key);
	}
	
	protected Collection<PropertyHolder> getAllProperties() {
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

}
