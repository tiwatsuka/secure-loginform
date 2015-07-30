package com.example.security.app.password;

import javax.inject.Inject;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.terasoluna.gfw.common.date.jodatime.JodaTimeDateFactory;

import com.example.security.app.validation.PasswordChangeValidator;
import com.example.security.domain.model.Account;
import com.example.security.domain.service.password.PasswordService;
import com.example.security.domain.service.userdetails.SampleUserDetails;

@Controller
@RequestMapping("password")
public class PasswordController {
	
	@Inject
	PasswordChangeValidator passwordChangeValidator;
	
	@Inject
	PasswordService passwordService;
	
	@Inject
	PasswordEncoder passwordEncoder;
	
	@Inject
	JodaTimeDateFactory dateFactory;
	
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
		
		Account account = new Account();
		account.setUsername(form.getUsername());
		account.setPassword(passwordEncoder.encode(form.getNewPassword()));
		account.setLastPasswordChangeDate(dateFactory.newDateTime());
		passwordService.updatePassword(account);
		
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
	
	@InitBinder("passwordForm")
	public void initBinder(WebDataBinder binder){
		binder.addValidators(passwordChangeValidator);
	}
}
