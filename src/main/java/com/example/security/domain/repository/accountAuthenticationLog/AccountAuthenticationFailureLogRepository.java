package com.example.security.domain.repository.accountAuthenticationLog;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.joda.time.DateTime;

import com.example.security.domain.model.AccountAuthenticationFailureLog;

public interface AccountAuthenticationFailureLogRepository {
	List<AccountAuthenticationFailureLog> findLogsByDulation(
			@Param("username") String username, @Param("from") DateTime from,
			@Param("to") DateTime to);
	
	int insert(AccountAuthenticationFailureLog accountAuthenticationLog);

	List<AccountAuthenticationFailureLog> findLatestLogs(@Param("username") String username, @Param("nbLog") long nbLog);
	
	int deleteByUsername(@Param("username") String username);
}
