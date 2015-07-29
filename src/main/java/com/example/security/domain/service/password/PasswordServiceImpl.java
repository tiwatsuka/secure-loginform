package com.example.security.domain.service.password;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.security.domain.model.Account;
import com.example.security.domain.repository.account.AccountRepository;

@Service
public class PasswordServiceImpl implements PasswordService {

	@Inject
	AccountRepository accountRepository;
	
	@Override
	@Transactional
	public boolean updatePassword(Account account) {
		return accountRepository.updatePassword(account);
	}

}
