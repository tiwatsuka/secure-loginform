package com.example.security.domain.service.passwordHistory;

import java.util.List;

import org.joda.time.DateTime;

import com.example.security.domain.model.PasswordHistory;

public interface PasswordHistorySharedService {
	
	public int insert(PasswordHistory history);
	
	public List<PasswordHistory> findByUseFrom(String username, DateTime useFrom);
	
	public List<PasswordHistory> findLatestHistory(String username, int limit);

}
