package com.example.security.app.validation;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.joda.time.DateTime;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.RuleResult;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.terasoluna.gfw.common.date.jodatime.JodaTimeDateFactory;

import com.example.security.app.validation.rule.EncodedPasswordHistoryRule;
import com.example.security.domain.model.Account;
import com.example.security.domain.model.PasswordHistory;
import com.example.security.domain.model.Role;
import com.example.security.domain.service.account.AccountSharedService;
import com.example.security.domain.service.passwordhistory.PasswordHistorySharedService;
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
	
	@Resource(name="characteristicPasswordValidator")
	PasswordValidator characteristicPasswordValidator;
	
	@Resource(name="usernamePasswordValidator")
	PasswordValidator usernamePasswordValidator;
	
	@Resource(name="encodedPasswordHistoryValidator")
	PasswordValidator encodedPasswordHistoryValidator;
	
	private String usernameField;
	
	private String newPasswordField;
	
	@Override
	public void initialize(ChangePassword constraintAnnotation) {
		usernameField = constraintAnnotation.idField();
		newPasswordField = constraintAnnotation.newPasswordField();
	}

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		BeanWrapper beanWrapper = new BeanWrapperImpl(value);
		String username = (String)beanWrapper.getPropertyValue(usernameField);
		String newPassword = (String)beanWrapper.getPropertyValue(newPasswordField);

		Account account = accountSharedService.findOne(username);
		String currentPassword = account.getPassword();
		Role role = account.getRole();
		
		context.disableDefaultConstraintViolation();
		boolean result = true;		
		result = checkCharacteristicsConstraints(newPassword, context);
		result = checkNewPasswordDifferentFromCurrentPassword(newPassword, currentPassword, context) && result;
		result = checkNotContainUsername(username, newPassword, context) && result;
		if(role.equals(Role.ADMN)){
			result = checkHistoricalPassword(username, newPassword, context) && result;
		}
		
		return result;
	}

	private boolean checkCharacteristicsConstraints(String newPassword, ConstraintValidatorContext context){
		RuleResult result = characteristicPasswordValidator.validate(new PasswordData(newPassword));
		if(result.isValid()){
			return true;
		}else{
			context.buildConstraintViolationWithTemplate(Joiner.on("\n").join(characteristicPasswordValidator.getMessages(result)))
				.addPropertyNode(newPasswordField)
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
		PasswordData passwordData = PasswordData.newInstance(newPassword, username, null);
		RuleResult result = usernamePasswordValidator.validate(passwordData);
		
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
		List<PasswordHistory> historyByTime = passwordHistorySharedService.findHistoriesByUseFrom(username, useFrom);
		List<PasswordHistory> historyByCount = passwordHistorySharedService.findLatestHistorys(username, 3);
		List<PasswordHistory> history = historyByCount.size() > historyByTime.size() ? historyByCount : historyByTime;
		
		List<PasswordData.Reference> historyData = new ArrayList<>();
		for(PasswordHistory h : history){
			historyData.add(new PasswordData.HistoricalReference(h.getPassword()));
		}
		
		PasswordData passwordData = PasswordData.newInstance(newPassword, username, historyData);
		RuleResult result = encodedPasswordHistoryValidator.validate(passwordData);
		
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
