package com.example.security.domain.service.account;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;
import org.terasoluna.gfw.common.date.jodatime.JodaTimeDateFactory;

import com.example.security.domain.model.AccountAuthenticationSuccessLog;
import com.example.security.domain.service.accountauthenticationlog.AccountAuthenticationLogSharedService;
import com.example.security.domain.service.userdetails.SampleUserDetails;

@Component
public class AccountAuthenticationSuccessEventListener implements
		ApplicationListener<AuthenticationSuccessEvent> {

	private static final Logger log = LoggerFactory.getLogger(AccountAuthenticationSuccessEventListener.class); 
	
	@Inject
	AccountAuthenticationLogSharedService accountAuthenticationLogSharedService;

	@Inject
	JodaTimeDateFactory dateFactory;
	
	@Override
	public void onApplicationEvent(AuthenticationSuccessEvent event) {
		log.info("ログイン成功時の処理をここに書けます -> {}", event);
		
		SampleUserDetails details = (SampleUserDetails) event.getAuthentication().getPrincipal();
		
		AccountAuthenticationSuccessLog log = new AccountAuthenticationSuccessLog();
		log.setUsername(details.getUsername());
		log.setAuthenticationTimestamp(dateFactory.newDateTime());
		
		accountAuthenticationLogSharedService.insertSuccessLog(log);
	}

}
