package com.example.security.app.passwordReissue;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.terasoluna.gfw.common.exception.ResourceNotFoundException;

import com.example.security.domain.model.PasswordReissueInfo;
import com.example.security.domain.service.passwordReissue.PasswordReissueService;

@Controller
@RequestMapping("/reissue")
public class PasswordReissueController {
	
	@Inject
	PasswordReissueService passwordReissueService;
	
	@RequestMapping(value = "create", params = "form")
	public String showCreateTokenForm(PasswordReissueForm form, Model model){
		return "passwordReissue/createTokenForm";
	}
	
	@RequestMapping(value="create", method=RequestMethod.POST)
	public String createToken(PasswordReissueForm form, RedirectAttributes attributes) {
		
		PasswordReissueInfo info = passwordReissueService.createReissueInfo(form.getUsername());
		String rowPassword = info.getSecret();

		try {
			passwordReissueService.saveReissueInfo(info);
		} catch (ResourceNotFoundException e) {
			return "redirect:/reissue/create?form";
		}
		
		attributes.addFlashAttribute("password", rowPassword);
		return "redirect:/reissue/create?complete";
	}
	
	@RequestMapping(value="create", params="complete")
	public String createTokenComplete(){
		return "passwordReissue/createTokenComplete";
	}
	
	@RequestMapping(value="resetPassword", params="form")
	public String showPasswordResetForm(PasswordResetForm form,
			Model model,
			@RequestParam("username") String username, @RequestParam("token") String token){

		try {
			passwordReissueService.findOne(username, token);
		} catch (ResourceNotFoundException e) {
			return "redirect:/";
		}

		form.setUsername(username);
		form.setToken(token);
		model.addAttribute("passwordResetForm", form);
		return "passwordReissue/passwordResetForm";
	}
	
	@RequestMapping(value="resetPassword", method=RequestMethod.POST)
	public String resetPassword(@Validated PasswordResetForm form,
			BindingResult bindingResult, Model model){
		if(bindingResult.hasErrors()){
			return "passwordReissue/passwordResetForm";
		}
		
		passwordReissueService.resetPassowrd(form.getUsername(), form.getToken(), form.getSecret(), form.getNewPassword());
		passwordReissueService.removeReissueInfo(form.getUsername(), form.getToken());
		
		return "redirect:/reissue/resetPassword?complete";
	}
	
	@RequestMapping(value="resetPassword", params="complete")
	public String resetPasswordComplete(){
		return "passwordReissue/passwordResetComplete";
	}
	
	@ModelAttribute("passwordReissueForm")
	public PasswordReissueForm setupReissueForm(){
		return new PasswordReissueForm();
	}
	
	@ModelAttribute("passwordResetForm")
	public PasswordResetForm setupResetForm(){
		return new PasswordResetForm();
	}
}
