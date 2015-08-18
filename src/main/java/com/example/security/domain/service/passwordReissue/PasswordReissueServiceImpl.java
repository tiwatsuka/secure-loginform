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

import com.example.security.domain.model.PasswordReissueInfo;
import com.example.security.domain.repository.passwordReissue.PasswordReissueInfoRepository;
import com.example.security.domain.service.account.AccountSharedService;

@Service
public class PasswordReissueServiceImpl implements PasswordReissueService {

	@Inject
	PasswordReissueInfoRepository passwordReissueInfoRepository;
	
	@Inject
	AccountSharedService accountSharedService;
	
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
		String password = generator.generatePassword(10, rules);
		
		DateTime expiryDate = DateTime.now().plusMinutes(tokenExpiration);
		
		PasswordReissueInfo info = new PasswordReissueInfo();
		info.setUsername(username);
		info.setToken(token);
		info.setPassword(password);
		info.setExpiryDate(expiryDate);
		
		return info;
	}

	@Override
	@Transactional
	public boolean SaveReissueInfo(PasswordReissueInfo info) {
		accountSharedService.findOne(info.getUsername());	//existence check
		
		info.setPassword(passwordEncoder.encode(info.getPassword()));
		
		passwordReissueInfoRepository.insert(info);
		
		return true; 
	}

}
