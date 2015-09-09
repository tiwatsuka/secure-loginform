package com.example.security.domain.service.passwordreissue;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

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
import org.terasoluna.gfw.common.date.jodatime.JodaTimeDateFactory;
import org.terasoluna.gfw.common.exception.BusinessException;
import org.terasoluna.gfw.common.exception.ResourceNotFoundException;
import org.terasoluna.gfw.common.exception.SystemException;
import org.terasoluna.gfw.common.message.ResultMessages;

import com.example.security.common.message.MessageKeys;
import com.example.security.domain.model.Account;
import com.example.security.domain.model.PasswordReissueInfo;
import com.example.security.domain.repository.passwordreissue.PasswordReissueFailureLogRepository;
import com.example.security.domain.repository.passwordreissue.PasswordReissueInfoRepository;
import com.example.security.domain.service.account.AccountSharedService;
import com.icegreen.greenmail.spring.GreenMailBean;

@Service
@Transactional
public class PasswordReissueServiceImpl implements PasswordReissueService {

	@Inject
	PasswordReissueFailureSharedService passwordReissueFailureSharedService;

	@Inject
	PasswordReissueInfoRepository passwordReissueInfoRepository;

	@Inject
	PasswordReissueFailureLogRepository passwordReissueFailureLogRepository;

	@Inject
	AccountSharedService accountSharedService;

	@Inject
	JodaTimeDateFactory dateFactory;

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

	@Resource(name = "passwordGenerationRules")
	List<CharacterRule> passwordGenerationRules;

	@Value("${tokenExpiration}")
	private int tokenExpiration;

	@Value("${app.hostAndPort}")
	private String hostAndPort;

	@Value("${app.contextPath}")
	private String contextPath;

	@Value("${app.passwordReissueProtocol}")
	private String protocol;

	private static final Logger logger = LoggerFactory
			.getLogger(PasswordReissueServiceImpl.class);

	@Override
	public PasswordReissueInfo createReissueInfo(String username) {

		String token = UUID.randomUUID().toString();

		String secret = passwordGenerator.generatePassword(10,
				passwordGenerationRules);

		DateTime expiryDate = dateFactory.newDateTime().plusMinutes(
				tokenExpiration);

		PasswordReissueInfo info = new PasswordReissueInfo();
		info.setUsername(username);
		info.setToken(token);
		info.setSecret(secret);
		info.setExpiryDate(expiryDate);

		return info;
	}

	@Override
	public boolean saveAndSendReissueInfo(PasswordReissueInfo info) {
		Account account = accountSharedService.findOne(info.getUsername()); // existence
																			// check

		info.setSecret(passwordEncoder.encode(info.getSecret()));

		int count = passwordReissueInfoRepository.insert(info);

		if (count > 0) {
			String passwordResetUrl = protocol + "://" + hostAndPort
					+ contextPath + "/reissue/resetpassword/?form&username="
					+ info.getUsername() + "&token=" + info.getToken();

			SimpleMailMessage message = new SimpleMailMessage(templateMessage);
			message.setTo(account.getEmail());
			message.setText(passwordResetUrl);
			mailSender.send(message);

			/* output received message to log for testing */
			try {
				MimeMessage[] receivedMessages = greenMailBean
						.getReceivedMessages();
				MimeMessage latestMessage = receivedMessages[receivedMessages.length - 1];
				if (logger.isDebugEnabled()) {
					logger.debug("From    : {}",
							latestMessage.getFrom()[0].toString());
					logger.debug("To      : {}", latestMessage
							.getRecipients(Message.RecipientType.TO)[0]
							.toString());
					logger.debug("Subject : {}", latestMessage.getSubject());
					logger.debug("Text    : {}", latestMessage.getContent());
				}
			} catch (IOException e) {
				throw new SystemException(MessageKeys.E_SL_FW_9001, e);
			} catch (MessagingException e) {
				throw new SystemException(MessageKeys.E_SL_FW_9001, e);
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	@Transactional(readOnly = true)
	public PasswordReissueInfo findOne(String username, String token) {
		PasswordReissueInfo info = passwordReissueInfoRepository.findOne(token);

		if (info == null) {
			throw new ResourceNotFoundException(ResultMessages.error().add(
					MessageKeys.E_SL_PR_5002, token));
		}
		if (!info.getUsername().equals(username)) {
			throw new BusinessException(ResultMessages.error().add(
					MessageKeys.E_SL_PR_5001));
		}

		if (info.getExpiryDate().isBefore(dateFactory.newDateTime())) {
			throw new BusinessException(ResultMessages.error().add(
					MessageKeys.E_SL_PR_2001));
		}

		return info;
	}

	@Override
	public boolean resetPassowrd(String username, String token, String secret,
			String rawPassword) {
		PasswordReissueInfo info = this.findOne(username, token);
		if (!passwordEncoder.matches(secret, info.getSecret())) {
			passwordReissueFailureSharedService.resetFailure(username, token);
			throw new BusinessException(ResultMessages.error().add(
					MessageKeys.E_SL_PR_5003));
		}
		passwordReissueInfoRepository.delete(token);
		passwordReissueFailureLogRepository.deleteByToken(token);

		return accountSharedService.updatePassword(username, rawPassword);

	}

	@Override
	public boolean removeExpired(DateTime date) {
		passwordReissueFailureLogRepository.deleteExpired(date);
		passwordReissueInfoRepository.deleteExpired(date);
		return true;
	}

}
