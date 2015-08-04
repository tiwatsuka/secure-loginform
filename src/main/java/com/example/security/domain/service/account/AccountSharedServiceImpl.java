package com.example.security.domain.service.account;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.terasoluna.gfw.common.date.jodatime.JodaTimeDateFactory;
import org.terasoluna.gfw.common.exception.ResourceNotFoundException;

import com.example.security.domain.model.Account;
import com.example.security.domain.model.AccountAuthenticationLog;
import com.example.security.domain.repository.account.AccountAuthenticationLogRepository;
import com.example.security.domain.repository.account.AccountRepository;

@Service
public class AccountSharedServiceImpl implements AccountSharedService {
	@Inject
	private AccountRepository accountRepository;
	
	@Inject
	private JodaTimeDateFactory dateFactory;
	
	@Inject
	private AccountAuthenticationLogRepository accountAuthenticationLogRepository;

	private int lockingDurationMinutes = 1;
	
	@Transactional(readOnly = true)
	@Override
	public Account findOne(String username) {
		Account account = accountRepository.findOne(username);
		
		if(account == null){
			throw new ResourceNotFoundException("The given account is not found! username = " + username);
		}
		return account;
	}

	@Override
	public boolean isLocked(String username) {
		DateTime lockedDate = accountRepository.findOne(username).getLockedDate();
		if(lockedDate != null){
			DateTime unlockDate = lockedDate.plusMinutes(lockingDurationMinutes);
			return dateFactory.newDateTime().isBefore(unlockDate);
		}else{
			return false;
		}
	}

	@Override
	public DateTime getLastLoginDate(String username) {
		List<AccountAuthenticationLog> logs = accountAuthenticationLogRepository.findBySuccess(username, true, 2);
		
		if(logs.size() <= 1){
			return null;
		}else{
			return logs.get(1).getAuthenticationTimestamp();
		}
	}

	@Transactional
	@Override
	public boolean lock(String username) {
		Account account = new Account();
		account.setUsername(username);
		account.setLockedDate(dateFactory.newDateTime());
		return accountRepository.updateLockedDate(account);
	}

	@Transactional
	@Override
	public boolean unlock(String username) {
		Account account = findOne(username);
		account.setLockedDate(null);
		return accountRepository.updateLockedDate(account);
	}
}

