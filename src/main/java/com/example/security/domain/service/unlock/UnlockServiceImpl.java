package com.example.security.domain.service.unlock;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.terasoluna.gfw.common.exception.BusinessException;
import org.terasoluna.gfw.common.message.ResultMessages;

import com.example.security.common.message.MessageKeys;
import com.example.security.domain.service.account.AccountSharedService;
import com.example.security.domain.service.accountauthenticationlog.AccountAuthenticationLogSharedService;

@Transactional
@Service
public class UnlockServiceImpl implements UnlockService {

	@Inject
	AccountSharedService accountSharedService;
	
	@Inject
	AccountAuthenticationLogSharedService accountAuthenticationLogSharedService;
	
	@Override
	public boolean unlock(String username) {
		if(!accountSharedService.isLocked(username)){
			throw new BusinessException(
					ResultMessages.error().add(MessageKeys.E_SL_UL_5001));
		}
		
		accountAuthenticationLogSharedService.deleteFailureLogByUsername(username);
		
		return true;
	}

}
