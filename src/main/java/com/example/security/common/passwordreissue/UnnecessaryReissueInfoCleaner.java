package com.example.security.common.passwordreissue;

import javax.inject.Inject;

import org.terasoluna.gfw.common.date.jodatime.JodaTimeDateFactory;

import com.example.security.domain.service.passwordreissue.PasswordReissueService;

public class UnnecessaryReissueInfoCleaner {

	@Inject
	PasswordReissueService passwordReissueService;

	@Inject
	JodaTimeDateFactory dateFactory;

	public void cleanup() {
		passwordReissueService.removeExpired(dateFactory.newDateTime());
	}

}
