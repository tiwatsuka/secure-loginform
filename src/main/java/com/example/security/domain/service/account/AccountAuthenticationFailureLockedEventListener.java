package com.example.security.domain.service.account;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureLockedEvent;
import org.springframework.stereotype.Component;

@Component
public class AccountAuthenticationFailureLockedEventListener implements
		ApplicationListener<AuthenticationFailureLockedEvent> {

	private static final Logger logger = LoggerFactory
			.getLogger(AccountAuthenticationFailureLockedEventListener.class);

	@Override
	public void onApplicationEvent(AuthenticationFailureLockedEvent event) {
		logger.info("ロックエラー発生時の処理をここに書けます -> {}", event);

	}

}
