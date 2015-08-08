package com.example.security.domain.service.account;

import org.joda.time.DateTime;

import com.example.security.domain.model.Account;

public interface AccountSharedService {
	Account findOne(String username);
	
	DateTime getLastLoginDate(String username);
	
	boolean unlock(String username);

	boolean isLocked(String username);

	public boolean isInitialPassword(String username);
	
	public boolean isCurrentPasswordExpired(String username);
	
}
