package com.example.security.domain.service.passwordReissue;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.terasoluna.gfw.common.exception.BusinessException;
import org.terasoluna.gfw.common.exception.ResourceNotFoundException;

import com.example.security.domain.model.PasswordReissueInfo;
import com.example.security.domain.repository.passwordReissue.PasswordReissueInfoRepository;
import com.example.security.domain.service.account.AccountSharedService;
import com.example.security.domain.service.passwordChange.PasswordChangeSharedService;
@Transactional
@Service
public class PasswordReissueServiceImpl implements PasswordReissueService {

	@Inject
	PasswordReissueInfoRepository passwordReissueInfoRepository;
	
	@Inject
	AccountSharedService accountSharedService;
	
	@Inject
	PasswordChangeSharedService passwordChangeSharedService;
	
	@Inject
	PasswordEncoder passwordEncoder;
	
	private final int tokenExpiration = 1; 
	
	@Override
	public PasswordReissueInfo createReissueInfo(String username) {
		
		String token = UUID.randomUUID().toString();

		List<CharacterRule> rules = Arrays.asList(
				new CharacterRule(EnglishCharacterData.UpperCase, 1), 
				new CharacterRule(EnglishCharacterData.LowerCase, 1),
				new CharacterRule(EnglishCharacterData.Digit, 1) 
				);
		PasswordGenerator generator = new PasswordGenerator();
		String secret = generator.generatePassword(10, rules);
		
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
	
		return (count > 0);
	}
	
	@Override
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
			// TODO insert failure log.
			throw new BusinessException("Invalid Secret");
		}
		
		return passwordChangeSharedService.updatePassword(username, rawPassword);

	}

}
