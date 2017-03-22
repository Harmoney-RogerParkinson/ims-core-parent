package com.harmoney.ims.core.database;

import java.time.LocalDateTime;
import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.harmoney.ims.core.database.descriptors.Result;
import com.sforce.soap.partner.sobject.SObject;

public abstract class AbstractSimpleDAO<T> extends AbstractDAO<T> {

	@Autowired UnpackHelper unpackHelper;

	@Transactional
	public Result createOrUpdate(SObject sobject) {
	
		String id = (String)sobject.getField("Id");
		T t = getById(id);
		Result result = null;
		if (t == null) {
			// new record
			try {
				t = clazz.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
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
	@Override
	protected void localInit() {
	}
	abstract protected void fieldUpdates(T object);

}
