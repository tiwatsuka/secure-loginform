package com.example.security.domain.repository.account;

import com.example.security.domain.model.Account;

public interface AccountRepository {
	Account findOne(String username);
	
	boolean updatePassword(Account account);
	
	boolean updateUnlockDate(Account account);
	
}
