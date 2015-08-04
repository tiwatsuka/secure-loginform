package com.example.security.app.unlock;

import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class UnlockForm {
	@Size(min = 1)
	private String username;
}
