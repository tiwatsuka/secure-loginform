package com.example.security.domain.service.passwordreissue;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletContext;

import org.joda.time.DateTime;
import org.passay.CharacterRule;
import org.passay.PasswordGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.terasoluna.gfw.common.exception.BusinessException;
import org.terasoluna.gfw.common.exception.ResourceNotFoundException;
import org.terasoluna.gfw.common.message.ResultMessages;

import com.example.security.domain.model.Account;
import com.example.security.domain.model.PasswordReissueFailureLog;
import com.example.security.domain.model.PasswordReissueInfo;
import com.example.security.domain.repository.passwordreissue.PasswordReissueFailureLogRepository;
import com.example.security.domain.repository.passwordreissue.PasswordReissueInfoRepository;
import com.example.security.domain.service.account.AccountSharedService;
import com.icegreen.greenmail.spring.GreenMailBean;

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
	PasswordEncoder passwordEncoder;
	
	@Inject
	PasswordGenerator passwordGenerator;
	
	@Inject
	JavaMailSender mailSender;
	
	@Inject
	SimpleMailMessage templateMessage;
	
	@Inject
	GreenMailBean greenMailBean;
	
	@Inject
	ServletContext servletContext;
	
	@Resource(name="passwordGenerationRules")
	List<CharacterRule> passwordGenerationRules;
	
	@Value("${tokenExpiration}")
	private int tokenExpiration;
	
	@Value("${tokenValidityThreshold}")
	private int tokenValidityThreshold;
	
	@Value("${app.hostAndPort}")
	private String hostAndPort;
	
	private static final Logger logger = LoggerFactory.getLogger(PasswordReissueServiceImpl.class); 
	
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
	public boolean saveAndSendReissueInfo(PasswordReissueInfo info) {
		Account account = accountSharedService.findOne(info.getUsername());	//existence check
		
		info.setSecret(passwordEncoder.encode(info.getSecret()));
		
		int count = passwordReissueInfoRepository.insert(info);
		
		if(count > 0){
			String passwordResetUrl = 
					"http://" + hostAndPort + servletContext.getContextPath() 
						+ "/reissue/resetpassword/?form&username=" + info.getUsername() + "&token=" + info.getToken(); 
			
			SimpleMailMessage message = new SimpleMailMessage(templateMessage);
			message.setTo(account.getEmail());
			message.setText(passwordResetUrl);
			mailSender.send(message);

			/* output received message to log for testing */
			try {
				MimeMessage[] receivedMessages= greenMailBean.getReceivedMessages();
				MimeMessage latestMessage = receivedMessages[receivedMessages.length-1]; 
				logger.debug("From    : " + latestMessage.getFrom()[0].toString());
				logger.debug("To      : " + latestMessage.getRecipients(Message.RecipientType.TO)[0].toString());
				logger.debug("Subject : " + latestMessage.getSubject());
				logger.debug("Text    : " + (String)latestMessage.getContent());
			} catch (IOException e) {
				e.printStackTrace();
			} catch (MessagingException e) {
				e.printStackTrace();
			}
			
			return true;
		}else{
			return false;
		}
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
			throw new ResourceNotFoundException(
					ResultMessages.error().add("com.example.security.domain.service.passwordreissue.PasswordReissueSerivce.findOne.ResourceNotFound", username, token)
					);
		}
		if(info.getExpiryDate().isBefore(DateTime.now())){
			throw new BusinessException(
					ResultMessages.error().add("com.example.security.domain.service.passwordreissue.PasswordReissueSerivce.findOne.expired")
					);
		}
		
		return info;
	}

	@Override
	public boolean resetPassowrd(String username, String token, String secret,
			String rawPassword) {
		PasswordReissueInfo info = this.findOne(username, token);
		if(!passwordEncoder.matches(secret, info.getSecret())){
			throw new BusinessException(
					ResultMessages.error().add("com.example.security.domain.service.passwordreissue.PasswordReissueSerivce.resetPassword")
					);
		}
		
		return accountSharedService.updatePassword(username, rawPassword);

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
