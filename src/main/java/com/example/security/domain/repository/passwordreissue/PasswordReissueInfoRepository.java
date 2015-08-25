package com.example.security.domain.repository.passwordreissue;

import org.apache.ibatis.annotations.Param;

import com.example.security.domain.model.PasswordReissueInfo;

public interface PasswordReissueInfoRepository {

	int insert(PasswordReissueInfo info);	

	PasswordReissueInfo findOne(@Param("username")String username, @Param("token")String token);

	int delete(@Param("username") String username, @Param("token") String token);
}
