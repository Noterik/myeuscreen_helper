package org.springfield.lou.myeuscreen.mapping;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springfield.lou.myeuscreen.publications.CollectionItem;

@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface SmithersToObjectSetter {
	String mapTo() default "";
}
