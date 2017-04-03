package com.harmoney.ims.core.messages;

import java.util.Map;
import java.util.concurrent.CountDownLatch;

public interface MessageHandler {

	public abstract void processMessage(Map<String, Object> message);
	public abstract void setLatch(CountDownLatch latch);
	public abstract String getRabbitQueue();

}