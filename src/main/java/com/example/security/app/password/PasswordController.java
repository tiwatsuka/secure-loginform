package com.example.security.app.password;

import javax.inject.Inject;

import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.example.security.domain.model.Account;
import com.example.security.domain.service.password.PasswordService;
import com.example.security.domain.service.userdetails.SampleUserDetails;

@Controller
@RequestMapping("password")
public class PasswordController {
	
	@Inject
	PasswordService passwordService;
	
	
	@RequestMapping(params="form")
	public String showForm(PasswordForm form,
			@AuthenticationPrincipal SampleUserDetails userDetails, Model model){
		
		Account account = userDetails.getAccount();
		model.addAttribute(account);
		return "password/changeForm";
	}
	
	@RequestMapping(method=RequestMethod.POST)
	public String change(@AuthenticationPrincipal SampleUserDetails userDetails,
			@Validated PasswordForm form,
			BindingResult bindingResult,
			Model model){
		
		if(bindingResult.hasErrors()){
			Account account = userDetails.getAccount();
			model.addAttribute(account);
			return "password/changeForm";
		}
		
		passwordService.updatePassword(form.getUsername(), form.getNewPassword());
		
		return "redirect:/password?complete";
	}
	
	@RequestMapping(method=RequestMethod.GET, params="complete")
	public String changeComplete(){
		return "password/changeFinish";
	}
	
	@ModelAttribute("passwordForm")
	public PasswordForm setUpPasswordForm() {
		return new PasswordForm();
	}
}
