/**
 * 
 */
package com.harmoney.ims.core.messages;

/**
 * @author Roger Parkinson
 *
 */
public class MessageConfigurationEntry {

	private final FieldResolver fieldResolver;
	private final String rabbitQueue;
	private final String pushTopic;
	private final static FieldResolver defaultFieldResolver = new FieldResolverGeneric();

	public MessageConfigurationEntry(String pushTopic, String rabbitQueue,
			FieldResolver fieldResolver) {
		this.pushTopic = pushTopic;
		this.rabbitQueue = rabbitQueue;
		this.fieldResolver = fieldResolver;
	}

	public MessageConfigurationEntry(String pushTopic, String rabbitQueue) {
		this(pushTopic,rabbitQueue,defaultFieldResolver);
	}

	public FieldResolver getFieldResolver() {
		return fieldResolver;
	}

	public String getRabbitQueue() {
		return rabbitQueue;
	}

	public String getPushTopic() {
		return pushTopic;
	}

}
