package com.example.security.domain.service.passwordHistory;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.security.domain.model.PasswordHistory;
import com.example.security.domain.repository.passwordHistory.PasswordHistoryRepository;

@Service
public class PasswordHistoryService {

	@Inject
	PasswordHistoryRepository passwordHistoryRepository;
	
	@Transactional
	public int insert(PasswordHistory history){
		return passwordHistoryRepository.insert(history);
	}
	
	@Transactional(readOnly=true)
	public List<PasswordHistory> findByUseFrom(DateTime useFrom){
		return passwordHistoryRepository.findByUseFrom(useFrom);
	}
}
