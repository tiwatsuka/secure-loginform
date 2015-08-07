package com.example.security.domain.service.userdetails;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.terasoluna.gfw.common.exception.ResourceNotFoundException;

import com.example.security.domain.model.Account;
import com.example.security.domain.model.PasswordHistory;
import com.example.security.domain.service.account.AccountSharedService;
import com.example.security.domain.service.passwordHistory.PasswordHistorySharedService;

@Service
public class SampleUserDetailsService implements UserDetailsService {
	
	@Inject
	AccountSharedService accountSharedService;
	
	@Inject
	PasswordHistorySharedService passwordHistorySharedService;
		
	@Transactional(readOnly = true)
	@Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {
		try {
			Account account = accountSharedService.findOne(username);
			List<PasswordHistory> history = passwordHistorySharedService.findLatestHistory(username, 1);
			
			boolean passwordExpired = false;
			boolean initialPassword = false;
			
			if(history.isEmpty()){
				initialPassword = true;
			}else{
				DateTime passwordExpiredDate = history.get(0).getUseFrom().plusMinutes(1);
				passwordExpired = DateTime.now().isAfter(passwordExpiredDate);
			}

			return new SampleUserDetails(account, passwordExpired, initialPassword, accountSharedService.isLocked(username));
		} catch (ResourceNotFoundException e) {
			throw new UsernameNotFoundException("user not found", e);
		}
	}

}
