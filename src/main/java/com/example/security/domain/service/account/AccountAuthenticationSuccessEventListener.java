package com.example.security.domain.service.account;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import com.example.security.domain.model.AccountAuthenticationLog;
import com.example.security.domain.repository.account.AccountAuthenticationLogRepository;
import com.example.security.domain.service.userdetails.SampleUserDetails;

@Component
public class AccountAuthenticationSuccessEventListener implements
		ApplicationListener<AuthenticationSuccessEvent> {

	private static final Logger log = LoggerFactory.getLogger(AccountAuthenticationSuccessEventListener.class); 
	
	@Inject
	AccountAuthenticationLogRepository accountAuthenticationLogRepository;
	
	@Override
	public void onApplicationEvent(AuthenticationSuccessEvent event) {
		log.info("ログイン成功時の処理をここに書けます -> {}", event);
		
		SampleUserDetails details = (SampleUserDetails) event.getAuthentication().getPrincipal();
		
		AccountAuthenticationLog accountAuthenticationLog = new AccountAuthenticationLog();
		accountAuthenticationLog.setUsername(details.getUsername());
		accountAuthenticationLog.setSuccess(true);
		accountAuthenticationLog.setAdministrativeActionForUnlock(false);
		accountAuthenticationLog.setAuthenticationTimestamp(DateTime.now());
		
		accountAuthenticationLogRepository.insert(accountAuthenticationLog);
	}

}
