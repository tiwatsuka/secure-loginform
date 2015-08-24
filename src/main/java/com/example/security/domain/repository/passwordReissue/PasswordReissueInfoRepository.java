package com.example.security.domain.repository.passwordreissue;

import org.apache.ibatis.annotations.Param;

import com.example.security.domain.model.PasswordReissueInfo;

public interface PasswordReissueInfoRepository {

	public int insert(PasswordReissueInfo info);	

	public PasswordReissueInfo findOne(@Param("username")String username, @Param("token")String token);

	public int delete(@Param("username") String username, @Param("token") String token);
}
