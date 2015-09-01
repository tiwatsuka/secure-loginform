package com.example.security.domain.model;

import lombok.Data;

import org.joda.time.DateTime;

@Data
public class PasswordReissueFailureLog {
	
	private String token;
	
	private DateTime attemptDate;
	
}
