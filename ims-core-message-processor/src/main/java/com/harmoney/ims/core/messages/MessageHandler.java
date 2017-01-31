package com.harmoney.ims.core.messages;

import java.util.Map;

public interface MessageHandler {

	public abstract void processMessage(Map<String, Object> message);

}