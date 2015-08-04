package com.example.security.app.validation.rule;

import javax.inject.Inject;

import org.passay.HistoryRule;
import org.passay.PasswordData;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class EncodedPasswordHistoryRule extends HistoryRule {

	@Inject
	PasswordEncoder passwordEncoder;
	
	@Override
	protected boolean matches(final String clearText, final PasswordData.Reference reference){
		return passwordEncoder.matches(clearText, reference.getPassword());
	}
}
