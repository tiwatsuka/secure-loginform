package com.example.security.common.validation;

import java.util.Arrays;

import javax.inject.Inject;

import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.Rule;
import org.passay.RuleResult;
import org.passay.UsernameRule;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.example.security.app.password.PasswordForm;
import com.example.security.domain.service.account.AccountSharedService;

@Component
public class PasswordChangeValidator implements Validator {

	@Inject
	AccountSharedService accountSharedService;
	
	@Inject
	PasswordEncoder passwordEncoder;
	
	@Override
	public boolean supports(Class<?> clazz) {
		return PasswordForm.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		if(errors.hasErrors()){
			return;
		}
		
		PasswordForm form = (PasswordForm) target;
		String currentPassword = accountSharedService.findOne(form.getUsername()).getPassword(); 
		
		if(!passwordEncoder.matches(form.getOldPassword(), currentPassword)){
			errors.rejectValue("oldPassword", "com.example.security.common.validation.PasswordChangeValidator.oldPassword",
					"It's not matched with current password.");
		}
		

		if(passwordEncoder.matches(form.getNewPassword(), currentPassword)){
			errors.rejectValue("newPassword", "com.example.security.common.validation.PasswordChangeValidator.samePassword",
					"Password must be changed.");
		}
		
		Rule usernameRule = new UsernameRule();
		PasswordData passwordData = new PasswordData(form.getNewPassword());
		passwordData.setUsername(form.getUsername());
		PasswordValidator passwordValidator = new PasswordValidator(Arrays.asList(usernameRule));
		RuleResult result = passwordValidator.validate(passwordData);
		
		if(!result.isValid()){
			errors.rejectValue("newPassword", "com.example.security.common.validation.PasswordChangeValidator.newPassword",
					"Password must not contains Username.");
		}
	}

}
