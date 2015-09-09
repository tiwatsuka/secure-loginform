package com.example.security.app.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.util.StringUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

public class ConfirmValidator implements ConstraintValidator<Confirm, Object> {
	private String field;

	private String confirmField;

	private String message;

	@Override
	public void initialize(Confirm constraintAnnotation) {
		field = constraintAnnotation.field();
		confirmField = "confirm" + StringUtils.capitalize(field);
		message = constraintAnnotation.message();
	}

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		BeanWrapper beanWrapper = new BeanWrapperImpl(value);
		Object fieldValue = beanWrapper.getPropertyValue(field);
		Object confirmFieldValue = beanWrapper.getPropertyValue(confirmField);
		boolean matched = ObjectUtils.nullSafeEquals(fieldValue,
				confirmFieldValue);
		if (matched) {
			return true;
		} else {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(message)
					.addPropertyNode(confirmField).addConstraintViolation();
			return false;
		}
	}

}
