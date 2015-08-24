package com.example.security.app.validation.rule;

import org.passay.HistoryRule;
import org.passay.PasswordData;
import org.springframework.security.crypto.password.PasswordEncoder;

public class EncodedPasswordHistoryRule extends HistoryRule {

	PasswordEncoder passwordEncoder;

	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}
	
	@Override
	protected boolean matches(final String clearText, final PasswordData.Reference reference){
		return passwordEncoder.matches(clearText, reference.getPassword());
	}
}
