package com.example.security.domain.repository.passwordHistory;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.joda.time.DateTime;

import com.example.security.domain.model.PasswordHistory;

public interface PasswordHistoryRepository {
	int insert(PasswordHistory history);
	
	List<PasswordHistory> findByUseFrom(@Param("username") String username, @Param("useFrom") DateTime useFrom);
	
	List<PasswordHistory> findLatestHistory(@Param("username") String username, @Param("limit") int limit);
}
