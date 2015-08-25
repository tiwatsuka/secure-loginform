package com.example.security.domain.repository.passwordreissue;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.example.security.domain.model.PasswordReissueFailureLog;

public interface PasswordReissueFailureLogRepository {
	
	List<PasswordReissueFailureLog> findByUsernameAndToken(@Param("username")String username, @Param("token")String token);
	
	int insert(PasswordReissueFailureLog log);
	
	int deleteByUsernameAndToken(@Param("username")String username, @Param("token")String token);
}
