package com.example.security.domain.service.password;

import javax.inject.Inject;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.security.domain.model.Account;
import com.example.security.domain.repository.account.AccountRepository;
import com.example.security.domain.service.userdetails.SampleUserDetails;

@Service
public class PasswordServiceImpl implements PasswordService {

	@Inject
	AccountRepository accountRepository;
	
	@Override
	@Transactional
	public boolean updatePassword(Account account) {
		boolean result = accountRepository.updatePassword(account); 
		
		/* When authenticated users update their passwords, update UserDetails in SecurityContextHolder. */
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();
		if(principal instanceof UserDetails){
			SampleUserDetails userDetails = (SampleUserDetails)principal;
			
			if(result && userDetails.getUsername().equals(account.getUsername())){
				Account newAccount = userDetails.getAccount();
				newAccount.setPassword(account.getPassword());
				newAccount.setLastPasswordChangeDate(account.getLastPasswordChangeDate());
				SampleUserDetails newUserDetails = new SampleUserDetails(newAccount, false, false, false);
				Authentication newAuthentication = new UsernamePasswordAuthenticationToken(
						newUserDetails, account.getPassword(), authentication.getAuthorities());
				SecurityContextHolder.getContext().setAuthentication(newAuthentication);
			}
		}
		
		return result;
	}

}
