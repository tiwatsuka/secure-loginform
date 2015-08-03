package com.example.security.domain.repository.account;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.joda.time.DateTime;

import com.example.security.domain.model.AccountAuthenticationLog;

public interface AccountAuthenticationLogRepository {
	List<AccountAuthenticationLog> findByDulation(
			@Param("username") String username, @Param("from") DateTime from,
			@Param("to") DateTime to);
	
	int insert(AccountAuthenticationLog accountAuthenticationLog);
	
	List<AccountAuthenticationLog> findBySuccess(
			@Param("username") String username, @Param("isSuccess") boolean isSuccess, @Param("nbLog") long nbLog);
	
	List<AccountAuthenticationLog> findLatestLogs(@Param("username") String username, @Param("nbLog") long nbLog);
}
