package com.example.security.domain.service.userdetails;

import org.joda.time.DateTime;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

import com.example.security.domain.model.Account;

public class SampleUserDetails extends User {
	private static final long serialVersionUID = 1L;
	
	private final Account account;
	
	private final DateTime lastLoginDate; 
	
	public SampleUserDetails(Account account, boolean isLocked, DateTime lastLoginDate) {
		super(account.getUsername(), account.getPassword(), true, true, true, !isLocked, 
				AuthorityUtils.createAuthorityList("ROLE_" + account.getRole().getCodeValue()));
		this.account = account;
		this.lastLoginDate = lastLoginDate;
	}
	
	public Account getAccount() {
		return account;
	}
	
	public DateTime getLastLoginDate(){
		return lastLoginDate;
	}

}
