package com.harmoney.ims.core.queuehandler.unpacker;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.helpers.MessageFormatter;

public class Result {
	
	private final List<String> errors = new ArrayList<>();
	private final List<String> warnings = new ArrayList<>();

	public String error(String messagePattern, Object arg) {
		String ret = MessageFormatter.format(messagePattern, arg).getMessage();
		errors.add(ret);
		return ret;
	}

	public String warn(String messagePattern, Object arg) {
		String ret = MessageFormatter.format(messagePattern, arg).getMessage();
		warnings.add(ret);
		return ret;
	}

	public String error(String messagePattern, Object arg1, Object arg2, Object arg3) {
		String ret = MessageFormatter.format(messagePattern, new Object[]{arg1, arg2, arg3}).getMessage();
		errors.add(ret);
		return ret;
	}

	public List<String> getErrors() {
		return errors;
	}

	public List<String> getWarnings() {
		return warnings;
	}
	public String toString() {
		return MessageFormatter.format("Unpacker errors: {} warnings: {}",errors.size(),warnings.size()).getMessage();
	}

}
