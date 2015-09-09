package com.example.security.domain.service.account;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.terasoluna.gfw.common.date.jodatime.JodaTimeDateFactory;
import org.terasoluna.gfw.common.exception.ResourceNotFoundException;
import org.terasoluna.gfw.common.message.ResultMessages;

import com.example.security.common.message.MessageKeys;
import com.example.security.domain.model.Account;
import com.example.security.domain.model.AccountAuthenticationFailureLog;
import com.example.security.domain.model.AccountAuthenticationSuccessLog;
import com.example.security.domain.model.PasswordHistory;
import com.example.security.domain.repository.account.AccountRepository;
import com.example.security.domain.service.accountauthenticationlog.AccountAuthenticationLogSharedService;
import com.example.security.domain.service.passwordhistory.PasswordHistorySharedService;

@Service
@Transactional
public class AccountSharedServiceImpl implements AccountSharedService {
	
	@Inject
	private AccountAuthenticationLogSharedService accountAuthenticationLogSharedService;
	
	@Inject
	private PasswordHistorySharedService passwordHistorySharedService;
	
	@Inject
	private AccountRepository accountRepository;
	
	@Inject
	PasswordEncoder passwordEncoder;
	
	@Inject
	JodaTimeDateFactory dateFactory;
	
	@Value("${lockingDurationMinutes}")
	private int lockingDurationMinutes;
	
	@Value("${lockingThreshold}")
	private int lockingThreshold;
	
	@Value("${passwordLifeTime}")
	private int passwordLifeTime;
	
	@Transactional(readOnly = true)
	@Override
	public Account findOne(String username) {
		Account account = accountRepository.findOne(username);
		
		if(account == null){
			throw new ResourceNotFoundException(
					ResultMessages.error().add(MessageKeys.E_SL_FA_5001, username)
					);
		}
		return account;
	}

	@Transactional(readOnly = true)
	@Override
	public boolean isLocked(String username) {
		List<AccountAuthenticationFailureLog> failureLogs = 
				accountAuthenticationLogSharedService.findLatestFailureLogs(username, lockingThreshold);

		if(failureLogs.size() < lockingThreshold){
			return false;
		}

		if(failureLogs.get(0).getAuthenticationTimestamp()
				.isBefore(dateFactory.newDateTime().minusMinutes(lockingDurationMinutes))){
			return false;
		}
		
		/* [Optional] 
		 * If you intend to treat strictly successive authentication failure, use following snippet.
		 *  
		List<AccountAuthenticationSuccessLog> successLogs = 
				accountAuthenticationLogSharedService.findLatestSuccessLogs(username, 1);
		if(successLogs.isEmpty()){
			return true;
		}
		
		AccountAuthenticationSuccessLog latestSuccessLog = successLogs.get(0); 
		for(AccountAuthenticationFailureLog failureLog : failureLogs){
			if(latestSuccessLog.getAuthenticationTimestamp().isAfter(failureLog.getAuthenticationTimestamp())){
				return false;
			}
		}
		*/

		return true;
	}

	@Transactional(readOnly = true)
	@Override
	public DateTime getLastLoginDate(String username) {
		List<AccountAuthenticationSuccessLog> logs = 
				accountAuthenticationLogSharedService.findLatestSuccessLogs(username, 1);
		
		if(logs.isEmpty()){
			return null;
		}else{
			return logs.get(0).getAuthenticationTimestamp();
		}
	}

	@Transactional(readOnly = true)
	@Override
	@Cacheable("isInitialPassword")
	public boolean isInitialPassword(String username) {
		List<PasswordHistory> passwordHistories = passwordHistorySharedService.findLatestHistories(username, 1); 
		return passwordHistories.isEmpty();
	}

	@Transactional(readOnly = true)
	@Override
	@Cacheable("isCurrentPasswordExpired")
	public boolean isCurrentPasswordExpired(String username) {
		List<PasswordHistory> passwordHistories = passwordHistorySharedService.findLatestHistories(username, 1);
		
		if(passwordHistories.isEmpty()){
			return true;
		}
		
		if(passwordHistories.get(0).getUseFrom().isBefore(dateFactory.newDateTime().minusMinutes(passwordLifeTime))){
			return true;
		}
		
		return false;
	}

	@Override
	@CacheEvict(value={"isInitialPassword", "isCurrentPasswordExpired"}, key="#username")
	public boolean updatePassword(String username, String rawPassword) {
		String password = passwordEncoder.encode(rawPassword);
		boolean result = accountRepository.updatePassword(username, password); 
		
		DateTime passwordChangeDate = dateFactory.newDateTime(); 
		
		PasswordHistory passwordHistory = new PasswordHistory();
		passwordHistory.setUsername(username);
		passwordHistory.setPassword(password);
		passwordHistory.setUseFrom(passwordChangeDate);
		passwordHistorySharedService.insert(passwordHistory);
		
		return result;
	}
	
	@Override
	@CacheEvict(value={"isInitialPassword", "isCurrentPasswordExpired"}, key="#username")
	public void clearPasswordValidationCache(String username){}
}

