package com.example.security.app.validation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.Rule;
import org.passay.RuleResult;
import org.passay.UsernameRule;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.terasoluna.gfw.common.date.jodatime.JodaTimeDateFactory;

import com.example.security.app.password.PasswordForm;
import com.example.security.app.validation.rule.EncodedPasswordHistoryRule;
import com.example.security.domain.model.Account;
import com.example.security.domain.model.PasswordHistory;
import com.example.security.domain.model.Role;
import com.example.security.domain.service.account.AccountSharedService;
import com.example.security.domain.service.passwordHistory.PasswordHistorySharedService;

@Component
public class PasswordChangeValidator implements Validator {

	@Inject
	AccountSharedService accountSharedService;
	
	@Inject
	PasswordHistorySharedService passwordHistorySharedService;
	
	@Inject
	PasswordEncoder passwordEncoder;
	
	@Inject
	JodaTimeDateFactory dateFactory;
	
	@Inject
	EncodedPasswordHistoryRule encodedPasswordHistoryRule;
	
	private final int passwordHistoryFrom = 3;
	
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
		Account account = accountSharedService.findOne(form.getUsername());
		String currentPassword = account.getPassword();
		Role role = account.getRole();
		
		checkOldPasswordMacheWithCurrentPassword(errors, form, currentPassword);
		checkNewPasswordDifferentFromCurrentPassword(errors, form, currentPassword);
		checkNotContainUsername(errors, form);
		if(role.equals(Role.ADMN)){
			checkHistoricalPassword(errors, form);
		}
	}
	
	private void checkOldPasswordMacheWithCurrentPassword(Errors errors, PasswordForm form, String currentPassword){
		if(!passwordEncoder.matches(form.getOldPassword(), currentPassword)){
			errors.rejectValue("oldPassword", "com.example.security.app.validation.PasswordChangeValidator.oldPassword",
					"It's not matched with current password.");
		}
	}
	
	private void checkNewPasswordDifferentFromCurrentPassword(Errors errors, PasswordForm form, String currentPassword){
		if(passwordEncoder.matches(form.getNewPassword(), currentPassword)){
			errors.rejectValue("newPassword", "com.example.security.app.validation.PasswordChangeValidator.samePassword",
					"Password must be changed.");
		}
	}
	
	private void checkNotContainUsername(Errors errors, PasswordForm form) {
		Rule usernameRule = new UsernameRule();
		PasswordData passwordData = new PasswordData(form.getNewPassword());
		passwordData.setUsername(form.getUsername());
		PasswordValidator passwordValidator = new PasswordValidator(Arrays.asList(usernameRule));
		RuleResult result = passwordValidator.validate(passwordData);
		
		if(!result.isValid()){
			errors.rejectValue("newPassword", "com.example.security.app.validation.PasswordChangeValidator.newPassword",
					"Password must not contains Username.");
		}
	}

	private void checkHistoricalPassword(Errors errors, PasswordForm form){
		DateTime useFrom = dateFactory.newDateTime().minusMinutes(passwordHistoryFrom);
		List<PasswordHistory> history = passwordHistorySharedService.findByUseFrom(form.getUsername(), useFrom);
		
		List<PasswordData.Reference> historyData = new ArrayList<>();
		for(PasswordHistory h : history){
			historyData.add(new PasswordData.HistoricalReference(h.getPassword()));
		}
		
		PasswordData passwordData = PasswordData.newInstance(form.getNewPassword(), form.getUsername(), historyData);
		PasswordValidator passwordValidator = new PasswordValidator(Arrays.asList((Rule)encodedPasswordHistoryRule));
		RuleResult result = passwordValidator.validate(passwordData);
		
		if(!result.isValid()){
			errors.rejectValue("newPassword", "com.example.security.app.validation.PasswordChangeValidator.passwordHistory",
					"You cannot use passwords which you have recently used.");
		}
	}
}
