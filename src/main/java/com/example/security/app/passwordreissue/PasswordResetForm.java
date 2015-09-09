package com.example.security.app.passwordreissue;

import com.example.security.app.validation.ProhibitReuse;
import com.example.security.app.validation.StrongPassword;
import com.example.security.app.validation.Confirm;

import lombok.Data;

@Data
@Confirm(field = "newPassword")
@StrongPassword(idField = "username", newPasswordField = "newPassword")
@ProhibitReuse(idField = "username", newPasswordField = "newPassword")
public class PasswordResetForm {

	private String username;

	private String token;

	private String secret;

	private String newPassword;

	private String confirmNewPassword;
}
