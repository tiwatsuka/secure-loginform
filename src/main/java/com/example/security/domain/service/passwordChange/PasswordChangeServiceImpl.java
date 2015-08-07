package com.example.security.domain.service.passwordChange;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.security.domain.model.Account;
import com.example.security.domain.model.PasswordHistory;
import com.example.security.domain.repository.account.AccountRepository;
import com.example.security.domain.service.passwordHistory.PasswordHistorySharedService;
import com.example.security.domain.service.userdetails.SampleUserDetails;

@Service
public class PasswordChangeServiceImpl implements PasswordChangeService {

	@Inject
	AccountRepository accountRepository;
	
	@Inject
	PasswordHistorySharedService passwordHistorySharedService;
	
	@Inject
	PasswordEncoder passwordEncoder;
	
	@Override
	@Transactional
	public boolean updatePassword(String username, String rawPassword) {
		String password = passwordEncoder.encode(rawPassword);
		boolean result = accountRepository.updatePassword(username, password); 
		
		DateTime passwordChangeDate = DateTime.now(); 
		
		PasswordHistory passwordHistory = new PasswordHistory();
		passwordHistory.setUsername(username);
		passwordHistory.setPassword(password);
		passwordHistory.setUseFrom(passwordChangeDate);
		passwordHistorySharedService.insert(passwordHistory);
		
		/* When authenticated users update their passwords, update UserDetails in SecurityContextHolder. */
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();
		if(principal instanceof UserDetails){
			SampleUserDetails userDetails = (SampleUserDetails)principal;
			
			if(result && userDetails.getUsername().equals(username)){
				Account newAccount = userDetails.getAccount();
				newAccount.setPassword(password);
				SampleUserDetails newUserDetails = new SampleUserDetails(newAccount, false, false, false);
				Authentication newAuthentication = new UsernamePasswordAuthenticationToken(
						newUserDetails, password, authentication.getAuthorities());
				SecurityContextHolder.getContext().setAuthentication(newAuthentication);				
			}
		}
		
		return result;
	}

}
