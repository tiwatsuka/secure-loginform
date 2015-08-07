package com.example.security.domain.service.passwordHistory;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.security.domain.model.PasswordHistory;
import com.example.security.domain.repository.passwordHistory.PasswordHistoryRepository;

@Service
public class PasswordHistorySharedServiceImpl implements PasswordHistorySharedService{

	@Inject
	PasswordHistoryRepository passwordHistoryRepository;
	
	private final int passwrodLifeTime = 1;
	
	@Transactional
	public int insert(PasswordHistory history){
		return passwordHistoryRepository.insert(history);
	}
	
	@Transactional(readOnly=true)
	public List<PasswordHistory> findByUseFrom(String username, DateTime useFrom){
		return passwordHistoryRepository.findByUseFrom(username, useFrom);
	}

	@Override
	public List<PasswordHistory> findLatestHistory(String username, int limit) {
		return passwordHistoryRepository.findLatestHistory(username, limit);
	}

	@Override
	@Cacheable("isInitialPassword")
	public boolean isInitialPassword(String username) {
		List<PasswordHistory> passwordHistories = findLatestHistory(username, 1); 
		return passwordHistories.isEmpty();
	}

	@Override
	@Cacheable("isCurrentPasswordExpired")
	public boolean isCurrentPasswordExpired(String username) {
		List<PasswordHistory> passwordHistories = findLatestHistory(username, 1);
		
		if(passwordHistories.isEmpty()){
			return true;
		}
		
		if(passwordHistories.get(0).getUseFrom().isBefore(DateTime.now().minusMinutes(passwrodLifeTime))){
			return true;
		}
		
		return false;
	}
}
