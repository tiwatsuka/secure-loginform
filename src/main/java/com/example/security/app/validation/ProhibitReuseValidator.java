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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.terasoluna.gfw.common.date.jodatime.JodaTimeDateFactory;

import com.example.security.domain.model.Account;
import com.example.security.domain.model.PasswordHistory;
import com.example.security.domain.model.Role;
import com.example.security.domain.service.account.AccountSharedService;
import com.example.security.domain.service.passwordhistory.PasswordHistorySharedService;

public class ProhibitReuseValidator implements
		ConstraintValidator<ProhibitReuse, Object> {

	@Inject
	AccountSharedService accountSharedService;

	@Inject
	PasswordHistorySharedService passwordHistorySharedService;

	@Inject
	PasswordEncoder passwordEncoder;

	@Inject
	JodaTimeDateFactory dateFactory;

	@Resource(name = "encodedPasswordHistoryValidator")
	PasswordValidator encodedPasswordHistoryValidator;

	@Value("${passwordHistoricalCheckingCount}")
	int passwordHistoricalCheckingCount;

	@Value("${passwordHistoricalCheckingMinutes}")
	int passwordHistoricalCheckingMinutes;

	private String usernameField;

	private String newPasswordField;

	private String message;

	@Override
	public void initialize(ProhibitReuse constraintAnnotation) {
		usernameField = constraintAnnotation.idField();
		newPasswordField = constraintAnnotation.newPasswordField();
		message = constraintAnnotation.message();
	}

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		BeanWrapper beanWrapper = new BeanWrapperImpl(value);
		String username = (String) beanWrapper.getPropertyValue(usernameField);
		String newPassword = (String) beanWrapper
				.getPropertyValue(newPasswordField);

		Account account = accountSharedService.findOne(username);
		String currentPassword = account.getPassword();

		context.disableDefaultConstraintViolation();
		boolean result = checkNewPasswordDifferentFromCurrentPassword(
				newPassword, currentPassword, context);
		if (result && account.getRoles().contains(Role.ADMN)) {
			result = checkHistoricalPassword(username, newPassword, context);
		}

		return result;
	}

	private boolean checkNewPasswordDifferentFromCurrentPassword(
			String newPassword, String currentPassword,
			ConstraintValidatorContext context) {
		if (!passwordEncoder.matches(newPassword, currentPassword)) {
			return true;
		} else {
			context.buildConstraintViolationWithTemplate(message)
					.addPropertyNode(newPasswordField).addConstraintViolation();
			return false;
		}
	}

	private boolean checkHistoricalPassword(String username,
			String newPassword, ConstraintValidatorContext context) {
		DateTime useFrom = dateFactory.newDateTime().minusMinutes(
				passwordHistoricalCheckingMinutes);
		List<PasswordHistory> historyByTime = passwordHistorySharedService
				.findHistoriesByUseFrom(username, useFrom);
		List<PasswordHistory> historyByCount = passwordHistorySharedService
				.findLatestHistories(username, passwordHistoricalCheckingCount);
		List<PasswordHistory> history = historyByCount.size() > historyByTime
				.size() ? historyByCount : historyByTime;

		List<PasswordData.Reference> historyData = new ArrayList<>();
		for (PasswordHistory h : history) {
			historyData.add(new PasswordData.HistoricalReference(h
					.getPassword()));
		}

		PasswordData passwordData = PasswordData.newInstance(newPassword,
				username, historyData);
		RuleResult result = encodedPasswordHistoryValidator
				.validate(passwordData);

		if (result.isValid()) {
			return true;
		} else {
			context.buildConstraintViolationWithTemplate(
					encodedPasswordHistoryValidator.getMessages(result).get(0))
					.addPropertyNode(newPasswordField).addConstraintViolation();
			return false;
		}
	}
}
