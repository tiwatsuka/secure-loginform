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

	private int lockingThreshold = 3;
	
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
		DateTime to = dateFactory.newDateTime();
		DateTime from = to.minusMinutes(lockingDurationMinutes);
		List<AccountAuthenticationLog> logs = accountAuthenticationLogRepository.findByDulation(username, from, to);
		int nbFailure = 0;
		
		for(AccountAuthenticationLog log : logs){
			if(log.isSuccess()){
				break;
			}else{
				++nbFailure;
			}
		}

		return nbFailure >= lockingThreshold;
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

}
