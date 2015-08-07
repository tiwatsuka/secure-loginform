package com.example.security.domain.repository.accountAuthenticationLog;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.example.security.domain.model.AccountAuthenticationSuccessLog;

public interface AccountAuthenticationSuccessLogRepository {
	
	int insert(AccountAuthenticationSuccessLog accountAuthenticationLog);

	List<AccountAuthenticationSuccessLog> findLatestLogs(@Param("username") String username, @Param("nbLog") long nbLog);
}
