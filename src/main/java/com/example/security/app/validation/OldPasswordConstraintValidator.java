package com.example.security.app.validation;

import javax.inject.Inject;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.security.domain.model.Account;
import com.example.security.domain.service.account.AccountSharedService;

public class OldPasswordConstraintValidator implements ConstraintValidator<ConfirmOldPassword, Object> {

	@Inject
	AccountSharedService accountSharedService;
	
	@Inject
	PasswordEncoder passwordEncoder;
	
	private String usernameField;
	
	private String oldPasswordField;
	
	@Override
	public void initialize(ConfirmOldPassword constraintAnnotation) {
		usernameField = constraintAnnotation.idField();
		oldPasswordField = constraintAnnotation.oldPasswordField();
		
	}

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		BeanWrapper beanWrapper = new BeanWrapperImpl(value);
		String username = (String)beanWrapper.getPropertyValue(usernameField);
		String oldPassword = (String)beanWrapper.getPropertyValue(oldPasswordField);

		Account account = accountSharedService.findOne(username);
		String currentPassword = account.getPassword();
		
		context.disableDefaultConstraintViolation();
		
		return checkOldPasswordMacheWithCurrentPassword(oldPassword, currentPassword, context);
	}

	private boolean checkOldPasswordMacheWithCurrentPassword(String oldPassword, String currentPassword, ConstraintValidatorContext context){
		if(passwordEncoder.matches(oldPassword, currentPassword)){
			return true;
		}else{
			context.buildConstraintViolationWithTemplate("{com.example.security.app.validation.OldPasswordConstraintValidator.checkOldPasswordMacheWithCurrentPassword}")
				.addPropertyNode(oldPasswordField)
				.addConstraintViolation();
			return false;
		}
	}
	
}
