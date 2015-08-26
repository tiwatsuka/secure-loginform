package com.example.security.domain.service.passwordhistory;

import java.util.List;

import org.joda.time.DateTime;

import com.example.security.domain.model.PasswordHistory;

public interface PasswordHistorySharedService {

	public int insert(PasswordHistory history);
	
	public List<PasswordHistory> findHistoriesByUseFrom(String username, DateTime useFrom);
	
	public List<PasswordHistory> findLatestHistories(String username, int limit);

}
