package com.example.security.domain.service.userdetails;

import java.util.List;

import org.joda.time.DateTime;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.example.security.domain.model.Account;

public class LoggedInUser extends User {
	private static final long serialVersionUID = 1L;

	private final Account account;

	private final DateTime lastLoginDate;

	public LoggedInUser(Account account, boolean isLocked,
			DateTime lastLoginDate, List<SimpleGrantedAuthority> authorities) {
		super(account.getUsername(), account.getPassword(), true, true, true,
				!isLocked, authorities);
		this.account = account;
		this.lastLoginDate = lastLoginDate;
	}

	public Account getAccount() {
		return account;
	}

	public DateTime getLastLoginDate() {
		return lastLoginDate;
	}

}
