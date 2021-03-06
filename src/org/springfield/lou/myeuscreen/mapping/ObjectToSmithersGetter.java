package org.springfield.lou.myeuscreen.mapping;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ObjectToSmithersGetter {
	String mapTo() default "";
}
