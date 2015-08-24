package com.example.security.domain.repository.passwordReissue;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.example.security.domain.model.PasswordReissueFailureLog;

public interface PasswordReissueFailureLogRepository {
	
	public List<PasswordReissueFailureLog> findByUsernameAndToken(@Param("username")String username, @Param("token")String token);
	
	public int insert(PasswordReissueFailureLog log);
	
	public int deleteByUsernameAndToken(@Param("username")String username, @Param("token")String token);
}
