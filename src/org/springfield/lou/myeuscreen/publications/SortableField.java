package org.springfield.lou.myeuscreen.publications;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface SortableField {
	String field() default "";

	boolean invert() default false;
}