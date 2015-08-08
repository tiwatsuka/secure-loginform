package com.example.security.domain.service.passwordHistory;

import java.util.List;

import org.joda.time.DateTime;

import com.example.security.domain.model.PasswordHistory;

public interface PasswordHistorySharedService {
	
	public boolean isInitialPassword(String username);
	
	public boolean isCurrentPasswordExpired(String username);
	
	public int insert(PasswordHistory history);
	
	public List<PasswordHistory> findHistoriesByUseFrom(String username, DateTime useFrom);
	
	public List<PasswordHistory> findLatestHistorys(String username, int limit);

}
