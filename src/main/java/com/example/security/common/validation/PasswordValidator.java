package com.example.security.common.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<Password, String> {

	@Override
	public void initialize(Password arg0) {

	}

	@Override
	public boolean isValid(String arg0, ConstraintValidatorContext arg1) {
		return "abc".equals(arg0);
	}

}
