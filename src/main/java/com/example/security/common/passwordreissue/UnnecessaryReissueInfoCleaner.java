package com.example.security.common.passwordreissue;

import javax.inject.Inject;

import org.joda.time.DateTime;

import com.example.security.domain.service.passwordreissue.PasswordReissueService;

public class UnnecessaryReissueInfoCleaner {

	
	@Inject
	PasswordReissueService passwordReissueService;
	
	public void cleanup(){
		passwordReissueService.removeExpired(DateTime.now());
	}
	
}
