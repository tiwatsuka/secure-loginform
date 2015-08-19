package com.example.security.domain.service.passwordChange;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.security.domain.model.PasswordHistory;
import com.example.security.domain.repository.account.AccountRepository;
import com.example.security.domain.service.passwordHistory.PasswordHistorySharedService;

@Service
public class PasswordChangeSharedServiceImpl implements PasswordChangeSharedService {

	@Inject
	AccountRepository accountRepository;
	
	@Inject
	PasswordHistorySharedService passwordHistorySharedService;
	
	@Inject
	PasswordEncoder passwordEncoder;
	
	@Override
	@Transactional
	@CacheEvict(value={"isInitialPassword", "isCurrentPasswordExpired"}, allEntries=true)
	public boolean updatePassword(String username, String rawPassword) {
		String password = passwordEncoder.encode(rawPassword);
		boolean result = accountRepository.updatePassword(username, password); 
		
		DateTime passwordChangeDate = DateTime.now(); 
		
		PasswordHistory passwordHistory = new PasswordHistory();
		passwordHistory.setUsername(username);
		passwordHistory.setPassword(password);
		passwordHistory.setUseFrom(passwordChangeDate);
		passwordHistorySharedService.insert(passwordHistory);
		
		return result;
	}

}
