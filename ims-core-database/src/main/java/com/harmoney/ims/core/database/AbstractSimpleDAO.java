package com.harmoney.ims.core.database;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.harmoney.ims.core.database.descriptors.Result;
import com.sforce.soap.partner.sobject.SObject;

public abstract class AbstractSimpleDAO<T> extends AbstractDAO<T> {
	
	private static final Logger log = LoggerFactory.getLogger(AbstractSimpleDAO.class);

	@Autowired UnpackHelper unpackHelper;

	@Transactional
	public Result createOrUpdate(SObject sobject) {
	
		String id = (String)sobject.getField("Id");
		T t = getById(id);
		Result result = null;
		if (t == null) {
			// new record
			t = getInstance();
			result = unpackHelper.unpack(sobject, t,objectDescriptor);
			fieldUpdates(t);
			entityManager.persist(t);
		} else {
			result = unpackHelper.unpack(sobject, t,objectDescriptor);
			fieldUpdates(t);
		}
		entityManager.flush();
		return result;
	}
	public T unpackMessage(Map<String, Object> map) {
		T ret = getInstance();
		Result result = objectDescriptor.unpack(map, ret);
        log.debug("{}",result);
		return ret;
	}
	public T unpack(SObject sobject) {
		T ret = getInstance();
		Result result =  unpackHelper.unpack(sobject, ret, objectDescriptor);
        log.debug("{}",result);
		return ret;
	}

	@Override
	protected void localInit() {
	}
	abstract protected void fieldUpdates(T object);

}
