package com.example.security.app.passwordchange;

import com.example.security.app.validation.Confirm;
import com.example.security.app.validation.ChangePassword;
import com.example.security.app.validation.ConfirmOldPassword;

import lombok.Data;

@Data
@Confirm(field = "newPassword")
@ChangePassword(idField="username", newPasswordField="newPassword")
@ConfirmOldPassword(idField="username", oldPasswordField="oldPassword")
public class PasswordChangeForm {
	
	private String username;

	private String oldPassword;
	
	private String newPassword;
	
	private String confirmNewPassword;
	
}
