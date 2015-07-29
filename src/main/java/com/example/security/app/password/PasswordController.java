package com.example.security.app.password;

import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.example.security.domain.model.Account;
import com.example.security.domain.service.userdetails.SampleUserDetails;

@Controller
@RequestMapping("password")
public class PasswordController {
	@RequestMapping(params="form")
	public String showForm(@Validated PasswordForm form,
			@AuthenticationPrincipal SampleUserDetails userDetails, Model model){
		
		Account account = userDetails.getAccount();
		model.addAttribute(account);
		return "password/changeForm";
	}
	
	@RequestMapping(method=RequestMethod.POST)
	public String change(@Validated PasswordForm form,
			BindingResult bindingResult,
			@AuthenticationPrincipal SampleUserDetails userDetails, Model model){
		
		if(bindingResult.hasErrors()){
			return "password/changeForm";
		}
		
		Account account = userDetails.getAccount();
		model.addAttribute(account);
		return "password/changeForm";
	}
	
	@ModelAttribute
	public PasswordForm setUpPasswordForm() {
		return new PasswordForm();
	}
}
