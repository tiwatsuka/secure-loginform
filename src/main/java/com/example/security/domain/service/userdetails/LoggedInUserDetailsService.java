package com.example.security.domain.service.userdetails;

import javax.inject.Inject;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.terasoluna.gfw.common.exception.ResourceNotFoundException;

import com.example.security.domain.model.Account;
import com.example.security.domain.service.account.AccountSharedService;

@Service
public class LoggedInUserDetailsService implements UserDetailsService {
	
	@Inject
	AccountSharedService accountSharedService;
		
	@Transactional(readOnly = true)
	@Override
	@CacheEvict(value={"isInitialPassword", "isCurrentPasswordExpired"}, allEntries=true)
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {
		try {
			Account account = accountSharedService.findOne(username);

			return new LoggedInUser(account, accountSharedService.isLocked(username), accountSharedService.getLastLoginDate(username));
		} catch (ResourceNotFoundException e) {
			throw new UsernameNotFoundException("user not found", e);
		}
	}

}
