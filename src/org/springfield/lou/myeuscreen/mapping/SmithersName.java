package org.springfield.lou.myeuscreen.mapping;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface SmithersName {
	public String name() default "";
}
