package com.example.security.domain.service.account;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

import com.example.security.domain.model.AccountAuthenticationFailureLog;
import com.example.security.domain.service.accountauthenticationlog.AccountAuthenticationLogSharedService;

@Component
public class AccountAuthenticationFailureBadCredentialsEventListener implements
		ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

	private static final Logger log = LoggerFactory.getLogger(AccountAuthenticationFailureBadCredentialsEventListener.class); 
	
	@Inject
	AccountAuthenticationLogSharedService accountAuthenticationLogSharedService;
	
	@Inject
	AccountSharedService accountSharedService;
	
	@Override
	public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
		log.info("ログイン失敗時の処理をここに書けます -> {}", event);
		
		String username = (String) event.getAuthentication().getPrincipal();
		
		AccountAuthenticationFailureLog log = new AccountAuthenticationFailureLog();
		log.setUsername(username);
		log.setAuthenticationTimestamp(DateTime.now());
						
		accountAuthenticationLogSharedService.insertFailureLog(log);
	}

}
