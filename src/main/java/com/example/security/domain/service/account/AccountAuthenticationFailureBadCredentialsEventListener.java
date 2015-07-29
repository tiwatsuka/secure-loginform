package com.example.security.domain.service.account;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

import com.example.security.domain.model.AccountAuthenticationLog;
import com.example.security.domain.repository.account.AccountAuthenticationLogRepository;

@Component
public class AccountAuthenticationFailureBadCredentialsEventListener implements
		ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

	private static final Logger log = LoggerFactory.getLogger(AccountAuthenticationFailureBadCredentialsEventListener.class); 
	
	@Inject
	AccountAuthenticationLogRepository accountAuthenticationLogRepository;
	
	@Override
	public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
		log.info("ログイン失敗時の処理をここに書けます -> {}", event);
		
		String username = (String) event.getAuthentication().getPrincipal();
		
		AccountAuthenticationLog accountAuthenticationLog = new AccountAuthenticationLog();
		accountAuthenticationLog.setUsername(username);
		accountAuthenticationLog.setSuccess(false);
		accountAuthenticationLog.setAdministrativeActionForUnlock(false);
		accountAuthenticationLog.setAuthenticationTimestamp(DateTime.now());
		
		accountAuthenticationLogRepository.insert(accountAuthenticationLog);
	}

}
