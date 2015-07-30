package com.example.security.domain.service.userdetails;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.terasoluna.gfw.common.date.jodatime.JodaTimeDateFactory;

import com.example.security.domain.model.Account;
import com.example.security.domain.service.account.AccountSharedService;

@Service
public class SampleUserDetailsService implements UserDetailsService {
	@Inject
	AccountSharedService accountSharedService;
	
	@Inject
	private JodaTimeDateFactory dateFactory;
	
	@Transactional(readOnly = true)
	@Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {
		try {
			Account account = accountSharedService.findOne(username);
			
			boolean passwordExpired = false;
			boolean initialPassword = false;
			DateTime lastPasswordChangeDate = account.getLastPasswordChangeDate();
			if(lastPasswordChangeDate == null){
				initialPassword = true;
			}else{
				DateTime passwordExpiredDate = lastPasswordChangeDate.plusMinutes(1);
				passwordExpired = dateFactory.newDateTime().isAfter(passwordExpiredDate);
			}

			return new SampleUserDetails(account, passwordExpired, initialPassword, accountSharedService.isLocked(username));
		} catch (Exception e) {
			throw new UsernameNotFoundException("user not found", e);
		}
	}

}
