package com.harmoney.ims.core.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)

public @interface SalesforceName {
	String fieldName() default "";
	String tableName() default "";
}
