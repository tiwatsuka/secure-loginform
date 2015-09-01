package com.example.security.domain.service.passwordreissue;

import org.joda.time.DateTime;

import com.example.security.domain.model.PasswordReissueInfo;

public interface PasswordReissueService {

	public PasswordReissueInfo createReissueInfo(String username);
	
	public boolean saveAndSendReissueInfo(PasswordReissueInfo info);
	
	public boolean removeReissueInfo(String token);
	
	public PasswordReissueInfo findOne(String token);
	
	public boolean resetPassowrd(String username, String token, String secret, String rawPassword);
	
	public void resetFailure(String username, String token);
	
	public boolean removeExpired(DateTime date);
}
