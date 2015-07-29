package com.example.security.app.password;

import com.example.security.common.validation.Password;

import lombok.Data;

@Data
public class PasswordForm {

	private String oldPassword;
	
	@Password
	private String newPassword;
	
	private String newPasswordConfirm;
	
}
