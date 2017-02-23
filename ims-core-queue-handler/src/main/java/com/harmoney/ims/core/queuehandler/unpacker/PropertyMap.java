package com.harmoney.ims.core.queuehandler.unpacker;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;

public class PropertyMap {
	
	private final Map<String,PropertyHolder> map = new HashMap<>();
	private final Class<?> clazz;
	
	protected PropertyMap(Class<?> clazz) {
		this.clazz = clazz;
	}

	protected void put(String name, Method writeMethod, Column column) {
		map.put(name, new PropertyHolder(writeMethod, column, clazz));
		
	}

	protected PropertyHolder get(String key) {
		return map.get(key);
	}

}
