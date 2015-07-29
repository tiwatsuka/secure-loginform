package com.example.security.domain.service.account;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureLockedEvent;
import org.springframework.stereotype.Component;

import com.example.security.domain.repository.account.AccountAuthenticationLogRepository;

@Component
public class AccountAuthenticationFailureLockedEventListener implements
		ApplicationListener<AuthenticationFailureLockedEvent> {

	private static final Logger log = LoggerFactory.getLogger(AccountAuthenticationFailureLockedEventListener.class); 
	
	@Inject
	AccountAuthenticationLogRepository accountAuthenticationLogRepository;
	
	@Override
	public void onApplicationEvent(AuthenticationFailureLockedEvent event) {
		log.info("ロックエラー発生時の処理をここに書けます -> {}", event);

	}

}
