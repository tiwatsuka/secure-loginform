package com.example.security.app.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = {ProhibitReuseValidator.class})
@Target({TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
public @interface ProhibitReuse {
	String message() default "{com.example.security.app.validation.ProhibitReuse.message}";
	
	Class<?>[] groups() default {};
	
	String idField();
	
	String newPasswordField();
	
	@Target({TYPE, ANNOTATION_TYPE})
	@Retention(RUNTIME)
	@Documented
	public @interface List {
		ProhibitReuse[] value();
	}
	
	Class<? extends Payload>[] payload() default {};
}
