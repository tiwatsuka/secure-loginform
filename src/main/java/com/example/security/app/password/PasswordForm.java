package com.example.security.app.password;

import com.example.security.app.validation.Confirm;
import com.example.security.app.validation.ChangePassword;

import lombok.Data;

@Data
@Confirm(field = "newPassword")
@ChangePassword(idField="username", newPasswordField="newPassword", oldPasswordField="oldPassword")
public class PasswordForm {
	
	private String username;

	private String oldPassword;
	
	private String newPassword;
	
	private String confirmNewPassword;
	
}
