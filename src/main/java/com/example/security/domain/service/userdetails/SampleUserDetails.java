package com.example.security.domain.service.userdetails;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

import com.example.security.domain.model.Account;

public class SampleUserDetails extends User {
	private static final long serialVersionUID = 1L;
	
	private final Account account;
	
	private final boolean passwordExpired;
	
	private final boolean initialPassword;
	
	public SampleUserDetails(Account account, boolean passwordExpired, boolean initialPassword, boolean isLocked) {
		super(account.getUsername(), account.getPassword(), true, true, true, !isLocked, AuthorityUtils.createAuthorityList("ROLE_USER"));
		this.account = account;
		this.passwordExpired = passwordExpired;
		this.initialPassword = initialPassword;
	}
	
	public Account getAccount() {
		return account;
	}
	
	public boolean isPasswordExpired() {
		return passwordExpired;
	}
	
	public boolean isInitialPassword() {
		return initialPassword;
	}

}
