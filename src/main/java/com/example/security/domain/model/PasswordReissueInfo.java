package com.example.security.domain.model;

import org.joda.time.DateTime;

import lombok.Data;

@Data
public class PasswordReissueInfo {

	private String username;
	
	private String token;
	
	private String secret;
	
	private DateTime expiryDate;
}
