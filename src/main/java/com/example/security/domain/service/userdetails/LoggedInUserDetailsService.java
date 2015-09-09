package com.example.security.domain.service.userdetails;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.terasoluna.gfw.common.exception.ResourceNotFoundException;

import com.example.security.domain.model.Account;
import com.example.security.domain.model.Role;
import com.example.security.domain.service.account.AccountSharedService;

@Service
public class LoggedInUserDetailsService implements UserDetailsService {

	@Inject
	AccountSharedService accountSharedService;

	@Transactional(readOnly = true)
	@Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {
		try {
			Account account = accountSharedService.findOne(username);
			List<SimpleGrantedAuthority> authorities = new ArrayList<>();
			for (Role role : account.getRoles()) {
				authorities.add(new SimpleGrantedAuthority("ROLE_"
						+ role.getCodeValue()));
			}
			return new LoggedInUser(account,
					accountSharedService.isLocked(username),
					accountSharedService.getLastLoginDate(username),
					authorities);
		} catch (ResourceNotFoundException e) {
			throw new UsernameNotFoundException("user not found", e);
		}
	}

}
