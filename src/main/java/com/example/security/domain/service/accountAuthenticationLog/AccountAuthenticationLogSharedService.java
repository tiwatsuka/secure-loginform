package com.example.security.domain.service.accountAuthenticationLog;

import java.util.List;

import org.joda.time.DateTime;

import com.example.security.domain.model.AccountAuthenticationFailureLog;
import com.example.security.domain.model.AccountAuthenticationSuccessLog;

public interface AccountAuthenticationLogSharedService {
	
	public List<AccountAuthenticationFailureLog> findFailureLogsByDulation(String username, DateTime from, DateTime to);
	
	public List<AccountAuthenticationSuccessLog> findLatestSuccessLogs(String username, int count);
	
	public List<AccountAuthenticationFailureLog> findLatestFailureLogs(String username, int count);
	
	public int insertSuccessLog(AccountAuthenticationSuccessLog log);
	
	public int insertFailureLog(AccountAuthenticationFailureLog log);
	
	public int deleteFailureLogByUsername(String username);
}
