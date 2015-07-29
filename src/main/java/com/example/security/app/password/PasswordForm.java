package com.example.security.app.password;

import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class PasswordForm {

	private String oldPassword;
	
	@Size(min=3)
	private String newPassword;
	
	private String newPasswordConfirm;
	
}
