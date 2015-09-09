package com.example.security.domain.repository.account;

import org.apache.ibatis.annotations.Param;

import com.example.security.domain.model.Account;

public interface AccountRepository {
	Account findOne(String username);

	boolean updatePassword(@Param("username") String username,
			@Param("password") String password);

}
