package com.example.security.domain.service.passwordhistory;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.security.domain.model.PasswordHistory;
import com.example.security.domain.repository.passwordhistory.PasswordHistoryRepository;

@Service
@Transactional
public class PasswordHistorySharedServiceImpl implements PasswordHistorySharedService{

	@Inject
	PasswordHistoryRepository passwordHistoryRepository;
	
	public int insert(PasswordHistory history){
		return passwordHistoryRepository.insert(history);
	}
	
	@Transactional(readOnly=true)
	public List<PasswordHistory> findHistoriesByUseFrom(String username, DateTime useFrom){
		return passwordHistoryRepository.findByUseFrom(username, useFrom);
	}

	@Override
	@Transactional(readOnly=true)
	public List<PasswordHistory> findLatestHistorys(String username, int limit) {
		return passwordHistoryRepository.findLatestHistories(username, limit);
	}

}
