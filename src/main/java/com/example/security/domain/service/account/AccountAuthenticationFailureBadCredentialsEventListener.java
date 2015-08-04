package com.example.security.domain.service.account;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;
import org.terasoluna.gfw.common.exception.ResourceNotFoundException;

import com.example.security.domain.model.AccountAuthenticationLog;
import com.example.security.domain.repository.account.AccountAuthenticationLogRepository;

@Component
public class AccountAuthenticationFailureBadCredentialsEventListener implements
		ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

	private static final Logger log = LoggerFactory.getLogger(AccountAuthenticationFailureBadCredentialsEventListener.class); 
	
	@Inject
	AccountAuthenticationLogRepository accountAuthenticationLogRepository;
	
	@Inject
	AccountSharedService accountSharedService;
	
	private final int lockingThreshold = 3;
	
	@Override
	public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
		log.info("ログイン失敗時の処理をここに書けます -> {}", event);
		
		String username = (String) event.getAuthentication().getPrincipal();
		
		AccountAuthenticationLog accountAuthenticationLog = new AccountAuthenticationLog();
		accountAuthenticationLog.setUsername(username);
		accountAuthenticationLog.setSuccess(false);
		accountAuthenticationLog.setAuthenticationTimestamp(DateTime.now());
						
		accountAuthenticationLogRepository.insert(accountAuthenticationLog);
		
		boolean userExists = true; 
		try {
			accountSharedService.findOne(username);		//user existence check
		} catch (ResourceNotFoundException e) {
			userExists = false;
		}
		if(userExists){
			DateTime unlockDate = accountSharedService.findOne(username).getUnlockDate();
			List<AccountAuthenticationLog> logs = accountAuthenticationLogRepository.findLatestLogs(username, lockingThreshold);
			int failureCount = 0;
			for(AccountAuthenticationLog log : logs){
				if(!log.isSuccess() && (unlockDate == null || log.getAuthenticationTimestamp().isAfter(unlockDate))){
					failureCount++;
				}
			}
			if(failureCount >= lockingThreshold){
				accountSharedService.lock(username);
			}
		}
	}

}
