package com.example.security.domain.repository.passwordreissue;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.joda.time.DateTime;

import com.example.security.domain.model.PasswordReissueFailureLog;

public interface PasswordReissueFailureLogRepository {

	List<PasswordReissueFailureLog> findByToken(@Param("token") String token);

	int insert(PasswordReissueFailureLog log);

	int deleteByToken(@Param("token") String token);

	int deleteExpired(@Param("date") DateTime date);
}
