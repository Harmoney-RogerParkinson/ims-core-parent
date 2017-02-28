package com.harmoney.ims.core.database;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.springframework.util.Assert;

import com.harmoney.ims.core.instances.InvestorLoanTransaction;

/**
 * One of these for each class of interest
 * Holds a cache of reflection data and the operations we need to use it for.
 * 
 * @author Roger Parkinson
 *
 */
public class ObjectDescriptor {
	
	private Method idMethod;
	private List<NegatableDescriptor> negatableDescriptors = new ArrayList<>();
	private Class<?> clazz;


	protected ObjectDescriptor(Class<?> clazz) {
		this.clazz = clazz;
	}

	protected void setIdMethod(Method readMethod) {
		idMethod = readMethod;	
	}

	protected void add(NegatableDescriptor negatableDescriptor) {
		negatableDescriptors.add(negatableDescriptor);
		
	}

	protected long getId(Object object) {
		Assert.notNull(idMethod,"idMethod must not be null");
		try {
			return (long)idMethod.invoke(object, new Object[]{});
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected void negate(Object target) {
		for (NegatableDescriptor bigDecimalDescriptor: negatableDescriptors) {
			bigDecimalDescriptor.negate(target);
		}
	}

}
