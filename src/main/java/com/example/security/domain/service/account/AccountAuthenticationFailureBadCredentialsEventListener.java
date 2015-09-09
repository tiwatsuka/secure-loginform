package com.example.security.domain.service.account;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;
import org.terasoluna.gfw.common.date.jodatime.JodaTimeDateFactory;

import com.example.security.domain.model.AccountAuthenticationFailureLog;
import com.example.security.domain.service.accountauthenticationlog.AccountAuthenticationLogSharedService;

@Component
public class AccountAuthenticationFailureBadCredentialsEventListener implements
		ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

	private static final Logger logger = LoggerFactory
			.getLogger(AccountAuthenticationFailureBadCredentialsEventListener.class);

	@Inject
	AccountAuthenticationLogSharedService accountAuthenticationLogSharedService;

	@Inject
	AccountSharedService accountSharedService;

	@Inject
	JodaTimeDateFactory dateFactory;

	@Override
	public void onApplicationEvent(
			AuthenticationFailureBadCredentialsEvent event) {
		logger.info("ログイン失敗時の処理をここに書けます -> {}", event);

		String username = (String) event.getAuthentication().getPrincipal();

		AccountAuthenticationFailureLog failureLog = new AccountAuthenticationFailureLog();
		failureLog.setUsername(username);
		failureLog.setAuthenticationTimestamp(dateFactory.newDateTime());

		accountAuthenticationLogSharedService.insertFailureLog(failureLog);
	}

}
