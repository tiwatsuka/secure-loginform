package com.example.security.app.passwordchange;

import com.example.security.app.validation.Confirm;
import com.example.security.app.validation.ProhibitReuse;
import com.example.security.app.validation.StrongPassword;
import com.example.security.app.validation.ConfirmOldPassword;

import lombok.Data;

@Data
@Confirm(field = "newPassword")
@StrongPassword(idField="username", newPasswordField="newPassword")
@ProhibitReuse(idField="username", newPasswordField="newPassword")
@ConfirmOldPassword(idField="username", oldPasswordField="oldPassword")
public class PasswordChangeForm {
	
	private String username;

	private String oldPassword;
	
	private String newPassword;
	
	private String confirmNewPassword;
	
}
