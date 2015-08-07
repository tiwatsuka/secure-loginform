package com.example.security.app.account;

import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.security.app.passwordChange.PasswordChangeForm;
import com.example.security.domain.model.Account;
import com.example.security.domain.service.userdetails.SampleUserDetails;

@Controller
@RequestMapping("account")
public class AccountController {

	@RequestMapping
	public String view(@AuthenticationPrincipal SampleUserDetails userDetails,
			Model model) {
		Account account = userDetails.getAccount();
		model.addAttribute(account);
		return "account/view";
	}
	
	@ModelAttribute
	public PasswordChangeForm setUpPasswordForm(){
		return new PasswordChangeForm();
	}
}
