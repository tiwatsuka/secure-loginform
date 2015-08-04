package com.example.security.domain.repository.passwordHistory;

import java.util.List;

import org.joda.time.DateTime;
import org.springframework.data.repository.query.Param;

import com.example.security.domain.model.PasswordHistory;

public interface PasswordHistoryRepository {
	int insert(@Param("history") PasswordHistory history);
	
	List<PasswordHistory> findByUseFrom(@Param("useFrom") DateTime useFrom);
}
