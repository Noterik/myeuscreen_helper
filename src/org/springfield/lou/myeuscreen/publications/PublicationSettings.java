package org.springfield.lou.myeuscreen.publications;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)

public @interface PublicationSettings {
	public String systemName();
	public String readableName();
	public String readablePlural();
}
