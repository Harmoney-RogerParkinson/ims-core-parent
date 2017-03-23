package com.harmoney.ims.core.messages;

import java.util.Map;

public class FieldResolverGeneric implements FieldResolver {

	@Override
	public void resolve(Map<String, Object> sobject) {
		// This doesn't do any special resolving
		// Most objects don't need the resolving facility and they use 
		// this dummy resolver.

	}

}
