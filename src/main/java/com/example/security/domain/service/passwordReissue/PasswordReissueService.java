package com.example.security.domain.service.passwordReissue;

import com.example.security.domain.model.PasswordReissueInfo;

public interface PasswordReissueService {

	public PasswordReissueInfo createReissueInfo(String username);
	
	public boolean saveReissueInfo(PasswordReissueInfo info);
}
