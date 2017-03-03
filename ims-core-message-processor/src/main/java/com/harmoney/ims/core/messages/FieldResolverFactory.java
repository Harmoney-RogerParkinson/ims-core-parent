package com.harmoney.ims.core.messages;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

@Component
public class FieldResolverFactory {
	
	Map<String,FieldResolver> map = new HashMap<>();
	
	public void processFields(String name, Map<String, Object> sobject) {
		FieldResolver resolver = map.get(name);
		if (resolver == null) {
			return;
		}
		resolver.resolve(sobject);
	}
	@PostConstruct
	public void init() {
		map.put("/topic/ILTIMS", new FieldResolverILT());
	}

}
