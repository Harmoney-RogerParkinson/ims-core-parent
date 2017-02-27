package com.harmoney.ims.core.database;

import java.lang.reflect.Method;
import java.math.BigDecimal;

import com.harmoney.ims.core.instances.InvestorLoanTransaction;

public class NegatableDescriptor {

	private final Method writeMethod;
	private final Method readMethod;
	private final int scale;

	public NegatableDescriptor(Method readMethod, Method writeMethod, int scale) {
		this.readMethod = readMethod;
		this.writeMethod = writeMethod;
		this.scale = scale;
	}

	public void negate(InvestorLoanTransaction target) {
		try {
			BigDecimal bigDecimal = (BigDecimal)readMethod.invoke(target);
			if (bigDecimal != null) {
				writeMethod.invoke(target, bigDecimal.negate().setScale(scale,BigDecimal.ROUND_HALF_DOWN));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
	}

}
