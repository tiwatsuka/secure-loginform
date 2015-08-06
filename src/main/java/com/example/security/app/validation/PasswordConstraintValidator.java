package com.example.security.app.validation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.joda.time.DateTime;
import org.passay.CharacterCharacteristicsRule;
import org.passay.DigitCharacterRule;
import org.passay.LengthRule;
import org.passay.LowercaseCharacterRule;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.Rule;
import org.passay.RuleResult;
import org.passay.SpecialCharacterRule;
import org.passay.UppercaseCharacterRule;
import org.passay.UsernameRule;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.terasoluna.gfw.common.date.jodatime.JodaTimeDateFactory;

import com.example.security.app.validation.rule.EncodedPasswordHistoryRule;
import com.example.security.domain.model.Account;
import com.example.security.domain.model.PasswordHistory;
import com.example.security.domain.model.Role;
import com.example.security.domain.service.account.AccountSharedService;
import com.example.security.domain.service.passwordHistory.PasswordHistorySharedService;
import com.google.common.base.Joiner;

public class PasswordConstraintValidator implements ConstraintValidator<ChangePassword, Object> {

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
	
	private PasswordValidator validator;
	
	private String usernameField;
	
	private String newPasswordField;
	
	private String oldPasswordField;
	
	@Override
	public void initialize(ChangePassword constraintAnnotation) {
		LengthRule lengthRule = new LengthRule();
		lengthRule.setMinimumLength(3);
		
		CharacterCharacteristicsRule characterCharacteristicsRule = new CharacterCharacteristicsRule();
		characterCharacteristicsRule.getRules().add(new UppercaseCharacterRule(1));
		characterCharacteristicsRule.getRules().add(new LowercaseCharacterRule(1));
		characterCharacteristicsRule.getRules().add(new DigitCharacterRule(1));
		characterCharacteristicsRule.getRules().add(new SpecialCharacterRule(1));
		characterCharacteristicsRule.setNumberOfCharacteristics(3);	// 3 of 4 rules must be satisfied
		
		validator = new PasswordValidator(Arrays.asList(lengthRule, characterCharacteristicsRule));
		
		usernameField = constraintAnnotation.idField();
		newPasswordField = constraintAnnotation.newPasswordField();
		oldPasswordField = constraintAnnotation.oldPasswordField();
		
	}

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		BeanWrapper beanWrapper = new BeanWrapperImpl(value);
		String username = (String)beanWrapper.getPropertyValue(usernameField);
		String newPassword = (String)beanWrapper.getPropertyValue(newPasswordField);
		String oldPassword = (String)beanWrapper.getPropertyValue(oldPasswordField);

		Account account = accountSharedService.findOne(username);
		String currentPassword = account.getPassword();
		Role role = account.getRole();
		
		context.disableDefaultConstraintViolation();
		boolean result = true;		
		result = checkCharacteristicsConstraints(newPassword, context);
		result = checkOldPasswordMacheWithCurrentPassword(oldPassword, currentPassword, context) && result;
		result = checkNewPasswordDifferentFromCurrentPassword(newPassword, currentPassword, context) && result;
		result = checkNotContainUsername(username, newPassword, context) && result;
		if(role.equals(Role.ADMN)){
			result = checkHistoricalPassword(username, newPassword, context) && result;
		}
		
		return result;
	}

	private boolean checkCharacteristicsConstraints(String newPassword, ConstraintValidatorContext context){
		RuleResult result = validator.validate(new PasswordData(newPassword));
		if(result.isValid()){
			return true;
		}else{
			context.buildConstraintViolationWithTemplate(Joiner.on("\n").join(validator.getMessages(result)))
				.addPropertyNode(newPasswordField)
				.addConstraintViolation();
			return false;
		}
	}
	
	private boolean checkOldPasswordMacheWithCurrentPassword(String oldPassword, String currentPassword, ConstraintValidatorContext context){
		if(passwordEncoder.matches(oldPassword, currentPassword)){
			return true;
		}else{
			context.buildConstraintViolationWithTemplate("{com.example.security.app.validation.PasswordConstraintValidator.checkOldPasswordMacheWithCurrentPassword}")
				.addPropertyNode(oldPasswordField)
				.addConstraintViolation();
			return false;
		}
	}
	
	private boolean checkNewPasswordDifferentFromCurrentPassword(String newPassword, String currentPassword, ConstraintValidatorContext context){
		if(!passwordEncoder.matches(newPassword, currentPassword)){
			return true;
		}else{
			context.buildConstraintViolationWithTemplate("{com.example.security.app.validation.PasswordConstraintValidator.checkNewPasswordDifferentFromCurrentPassword}")
				.addPropertyNode(newPasswordField)
				.addConstraintViolation();
			return false;
		}
	}
	
	private boolean checkNotContainUsername(String username, String newPassword, ConstraintValidatorContext context) {
		Rule usernameRule = new UsernameRule();
		PasswordData passwordData = PasswordData.newInstance(newPassword, username, null);
		PasswordValidator passwordValidator = new PasswordValidator(Arrays.asList(usernameRule));
		RuleResult result = passwordValidator.validate(passwordData);
		
		if(result.isValid()){
			return true;
		}else{
			context.buildConstraintViolationWithTemplate("{com.example.security.app.validation.PasswordConstraintValidator.checkNotContainUsername}")
				.addPropertyNode(newPasswordField)
				.addConstraintViolation();
			return false;
		}
	}

	private boolean checkHistoricalPassword(String username, String newPassword, ConstraintValidatorContext context){
		DateTime useFrom = dateFactory.newDateTime().minusMinutes(3);
		List<PasswordHistory> historyByTime = passwordHistorySharedService.findByUseFrom(username, useFrom);
		List<PasswordHistory> historyByCount = passwordHistorySharedService.findLatestHistory(username, 3);
		List<PasswordHistory> history = historyByCount.size() > historyByTime.size() ? historyByCount : historyByTime;
		
		List<PasswordData.Reference> historyData = new ArrayList<>();
		for(PasswordHistory h : history){
			historyData.add(new PasswordData.HistoricalReference(h.getPassword()));
		}
		
		PasswordData passwordData = PasswordData.newInstance(newPassword, username, historyData);
		PasswordValidator passwordValidator = new PasswordValidator(Arrays.asList((Rule)encodedPasswordHistoryRule));
		RuleResult result = passwordValidator.validate(passwordData);
		
		if(result.isValid()){
			return true;
		}else{
			context.buildConstraintViolationWithTemplate("{com.example.security.app.validation.PasswordConstraintValidator.checkHistoricalPassword}")
				.addPropertyNode(newPasswordField)
				.addConstraintViolation();
			return false;
		}
	}	
}
