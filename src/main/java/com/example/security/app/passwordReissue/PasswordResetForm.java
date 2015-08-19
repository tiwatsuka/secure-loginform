package com.example.security.app.passwordReissue;

import com.example.security.app.validation.Confirm;

import lombok.Data;

@Data
@Confirm(field = "newPassword")
public class PasswordResetForm {
	
	private String username;
	
	private String token;
	
	private String secret;
	
	private String newPassword;
	
	private String confirmNewPassword;
}
