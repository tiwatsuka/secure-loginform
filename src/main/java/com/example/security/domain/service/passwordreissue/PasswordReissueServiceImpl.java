package com.example.security.domain.service.passwordreissue;

import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.joda.time.DateTime;
import org.passay.CharacterRule;
import org.passay.PasswordGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.terasoluna.gfw.common.exception.BusinessException;
import org.terasoluna.gfw.common.exception.ResourceNotFoundException;

import com.example.security.domain.model.PasswordReissueFailureLog;
import com.example.security.domain.model.PasswordReissueInfo;
import com.example.security.domain.repository.passwordreissue.PasswordReissueFailureLogRepository;
import com.example.security.domain.repository.passwordreissue.PasswordReissueInfoRepository;
import com.example.security.domain.service.account.AccountSharedService;
import com.example.security.domain.service.passwordchange.PasswordChangeSharedService;

@Service
@Transactional
public class PasswordReissueServiceImpl implements PasswordReissueService {

	@Inject
	PasswordReissueInfoRepository passwordReissueInfoRepository;
	
	@Inject
	PasswordReissueFailureLogRepository passwordReissueFailureLogRepository;
	
	@Inject
	AccountSharedService accountSharedService;
	
	@Inject
	PasswordChangeSharedService passwordChangeSharedService;
	
	@Inject
	PasswordEncoder passwordEncoder;
	
	@Inject
	PasswordGenerator passwordGenerator;
	
	@Resource(name="passwordGenerationRules")
	List<CharacterRule> passwordGenerationRules;
	
	@Value("${tokenExpiration}")
	private int tokenExpiration;
	
	@Value("${tokenValidityThreshold}")
	private int tokenValidityThreshold;
	
	@Override
	public PasswordReissueInfo createReissueInfo(String username) {
		
		String token = UUID.randomUUID().toString();

		String secret = passwordGenerator.generatePassword(10, passwordGenerationRules);
		
		DateTime expiryDate = DateTime.now().plusMinutes(tokenExpiration);
		
		PasswordReissueInfo info = new PasswordReissueInfo();
		info.setUsername(username);
		info.setToken(token);
		info.setSecret(secret);
		info.setExpiryDate(expiryDate);
		
		return info;
	}

	@Override
	public boolean saveReissueInfo(PasswordReissueInfo info) {
		accountSharedService.findOne(info.getUsername());	//existence check
		
		info.setSecret(passwordEncoder.encode(info.getSecret()));
		
		int count = passwordReissueInfoRepository.insert(info);
		
		return (count > 0); 
	}

	@Override
	public boolean removeReissueInfo(String username, String token) {
		
		int count = passwordReissueInfoRepository.delete(username, token);
		passwordReissueFailureLogRepository.deleteByUsernameAndToken(username, token);
		
		return (count > 0);
	}
	
	@Override
	@Transactional(readOnly=true)
	public PasswordReissueInfo findOne(String username, String token) {
		PasswordReissueInfo info = passwordReissueInfoRepository.findOne(username, token);

		if(info == null){
			throw new ResourceNotFoundException("Given pair of username and token was not found.");
		}
		if(info.getExpiryDate().isBefore(DateTime.now())){
			throw new BusinessException("The URL has expired.");
		}
		
		return info;
	}

	@Override
	public boolean resetPassowrd(String username, String token, String secret,
			String rawPassword) {
		PasswordReissueInfo info = this.findOne(username, token);
		if(!passwordEncoder.matches(secret, info.getSecret())){
			throw new BusinessException("Invalid Secret");
		}
		
		return passwordChangeSharedService.updatePassword(username, rawPassword);

	}

	@Override
	public void resetFailure(String username, String token) {
		PasswordReissueFailureLog log = new PasswordReissueFailureLog();
		log.setUsername(username);
		log.setToken(token);
		log.setAttemptDate(DateTime.now());
		passwordReissueFailureLogRepository.insert(log);

		List<PasswordReissueFailureLog> logs = passwordReissueFailureLogRepository.findByUsernameAndToken(username, token);
		if(logs.size() >= tokenValidityThreshold){
			removeReissueInfo(username, token);
		}
	}

}
