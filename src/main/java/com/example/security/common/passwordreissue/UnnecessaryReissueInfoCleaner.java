package com.example.security.common.passwordreissue;

import javax.inject.Inject;

import org.joda.time.DateTime;

import com.example.security.domain.repository.passwordreissue.PasswordReissueInfoRepository;

public class UnnecessaryReissueInfoCleaner {

	
	@Inject
	PasswordReissueInfoRepository passwordReissueInfoRepository;
	
	public void cleanup(){
		passwordReissueInfoRepository.deleteExpired(DateTime.now());
	}
	
}
