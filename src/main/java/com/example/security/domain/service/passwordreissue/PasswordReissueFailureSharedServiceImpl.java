package com.example.security.domain.service.passwordreissue;

import java.util.List;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.terasoluna.gfw.common.date.jodatime.JodaTimeDateFactory;

import com.example.security.domain.model.PasswordReissueFailureLog;
import com.example.security.domain.repository.passwordreissue.PasswordReissueFailureLogRepository;
import com.example.security.domain.repository.passwordreissue.PasswordReissueInfoRepository;

@Service
@Transactional
public class PasswordReissueFailureSharedServiceImpl implements
		PasswordReissueFailureSharedService {
	
	@Inject
	PasswordReissueFailureLogRepository passwordReissueFailureLogRepository;
	
	@Inject
	JodaTimeDateFactory dateFactory;
	
	@Inject
	PasswordReissueInfoRepository passwordReissueInfoRepository;
	
	@Value("${tokenValidityThreshold}")
	private int tokenValidityThreshold;
	
	
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	@Override
	public void resetFailure(String username, String token) {
		PasswordReissueFailureLog log = new PasswordReissueFailureLog();
		log.setToken(token);
		log.setAttemptDate(dateFactory.newDateTime());
		passwordReissueFailureLogRepository.insert(log);

		List<PasswordReissueFailureLog> logs = passwordReissueFailureLogRepository.findByToken(token);
		if(logs.size() >= tokenValidityThreshold){
			passwordReissueInfoRepository.delete(token);
			passwordReissueFailureLogRepository.deleteByToken(token);
		}
	}

}
