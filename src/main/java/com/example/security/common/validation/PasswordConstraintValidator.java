package com.example.security.common.validation;

import java.util.Arrays;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.passay.CharacterCharacteristicsRule;
import org.passay.DigitCharacterRule;
import org.passay.LengthRule;
import org.passay.LowercaseCharacterRule;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.RuleResult;
import org.passay.SpecialCharacterRule;
import org.passay.UppercaseCharacterRule;

import com.google.common.base.Joiner;

public class PasswordConstraintValidator implements ConstraintValidator<Password, String> {

	private PasswordValidator validator;
	
	@Override
	public void initialize(Password arg0) {
		LengthRule lengthRule = new LengthRule();
		lengthRule.setMinimumLength(3);
		
		CharacterCharacteristicsRule characterCharacteristicsRule = new CharacterCharacteristicsRule();
		characterCharacteristicsRule.getRules().add(new UppercaseCharacterRule(1));
		characterCharacteristicsRule.getRules().add(new LowercaseCharacterRule(1));
		characterCharacteristicsRule.getRules().add(new DigitCharacterRule(1));
		characterCharacteristicsRule.getRules().add(new SpecialCharacterRule(1));
		characterCharacteristicsRule.setNumberOfCharacteristics(3);	// 3 of 4 rules must be satisfied
		
		validator = new PasswordValidator(Arrays.asList(lengthRule, characterCharacteristicsRule));
		
	}

	@Override
	public boolean isValid(String password, ConstraintValidatorContext context) {
		RuleResult result = validator.validate(new PasswordData(password));
		if(result.isValid()){
			return true;
		}else{
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(Joiner.on("\n").join(validator.getMessages(result)))
				.addConstraintViolation();
			return false;
		}
	}

}
