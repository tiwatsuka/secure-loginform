package com.example.security.domain.service.accountAuthenticationLog;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.security.domain.model.AccountAuthenticationFailureLog;
import com.example.security.domain.model.AccountAuthenticationSuccessLog;
import com.example.security.domain.repository.accountAuthenticationLog.AccountAuthenticationFailureLogRepository;
import com.example.security.domain.repository.accountAuthenticationLog.AccountAuthenticationSuccessLogRepository;

@Service
@Transactional
public class AccountAuthenticationLogSharedServiceImpl implements AccountAuthenticationLogSharedService {

	@Inject
	AccountAuthenticationFailureLogRepository failureLogRepository;
	
	@Inject
	AccountAuthenticationSuccessLogRepository successRepository;
	
	@Transactional(readOnly=true)
	@Override
	public List<AccountAuthenticationFailureLog> findFailureLogsByDulation(String username, DateTime from, DateTime to) {
		return failureLogRepository.findLogsByDulation(username, from, to);
	}
	
	@Transactional(readOnly=true)
	@Override
	public List<AccountAuthenticationSuccessLog> findLatestSuccessLogs(String username, int count) {
		return successRepository.findLatestLogs(username, count);
	}

	@Transactional(readOnly=true)
	@Override
	public List<AccountAuthenticationFailureLog> findLatestFailureLogs(String username, int count) {
		return failureLogRepository.findLatestLogs(username, count);
	}

	@Override
	public int insertSuccessLog(AccountAuthenticationSuccessLog log) {
		return successRepository.insert(log);
	}

	@Override
	public int insertFailureLog(AccountAuthenticationFailureLog log) {
		return failureLogRepository.insert(log);
	}

	@Override
	public int deleteFailureLogByUsername(String username) {
		return failureLogRepository.deleteByUsername(username);
	}

}
