package com.example.security.app.password;

import com.example.security.app.validation.Confirm;
import com.example.security.app.validation.Password;

import lombok.Data;

@Data
@Confirm(field = "newPassword")
public class PasswordForm {
	
	private String username;

	private String oldPassword;
	
	@Password
	private String newPassword;
	
	private String confirmNewPassword;
	
}
