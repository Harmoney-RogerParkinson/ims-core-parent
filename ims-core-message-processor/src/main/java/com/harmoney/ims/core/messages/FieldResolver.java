package com.harmoney.ims.core.messages;

import java.util.Map;

public interface FieldResolver {

	void resolve(Map<String, Object> sobject);

}
