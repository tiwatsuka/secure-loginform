package com.example.security.app.passwordreissue;

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
import org.terasoluna.gfw.common.exception.BusinessException;
import org.terasoluna.gfw.common.exception.ResourceNotFoundException;

import com.example.security.domain.model.PasswordReissueInfo;
import com.example.security.domain.service.passwordreissue.PasswordReissueService;

@Controller
@RequestMapping("/reissue")
public class PasswordReissueController {
	
	@Inject
	PasswordReissueService passwordReissueService;
	
	@RequestMapping(value = "create", params = "form")
	public String showCreateTokenForm(CreateTokenForm form, Model model){
		return "passwordreissue/createTokenForm";
	}
	
	@RequestMapping(value="create", method=RequestMethod.POST)
	public String createToken(@Validated CreateTokenForm form, BindingResult bindingResult, Model model, RedirectAttributes attributes) {
		if(bindingResult.hasErrors()){
			return showCreateTokenForm(form, model);
		}
		
		PasswordReissueInfo info = passwordReissueService.createReissueInfo(form.getUsername());
		String rowPassword = info.getSecret();

		try {
			passwordReissueService.saveAndSendReissueInfo(info);
			attributes.addFlashAttribute("password", rowPassword);
			return "redirect:/reissue/create?complete";
		} catch (ResourceNotFoundException e) {
			model.addAttribute(e.getResultMessages());
			return showCreateTokenForm(form, model);
		}
	}
	
	@RequestMapping(value="create", params="complete")
	public String createTokenComplete(){
		return "passwordreissue/createTokenComplete";
	}
	
	@RequestMapping(value="resetpassword", params="form")
	public String showPasswordResetForm(PasswordResetForm form,
			Model model,
			@RequestParam("username") String username, @RequestParam("token") String token){

		try {
			passwordReissueService.findOne(username, token);
			form.setUsername(username);
			form.setToken(token);
			model.addAttribute("passwordResetForm", form);
			return "passwordreissue/passwordResetForm";
		} catch (ResourceNotFoundException e) {
			return "redirect:/";
		}
	}
	
	@RequestMapping(value="resetpassword", method=RequestMethod.POST)
	public String resetPassword(@Validated PasswordResetForm form, BindingResult bindingResult, Model model){
		if(bindingResult.hasErrors()){
			return showPasswordResetForm(form, model, form.getUsername(), form.getToken());
		}

		try {
			passwordReissueService.resetPassowrd(form.getUsername(), form.getToken(), form.getSecret(), form.getNewPassword());
			passwordReissueService.removeReissueInfo(form.getUsername(), form.getToken());
			return "redirect:/reissue/resetpassword?complete";
		} catch (BusinessException e) {
			passwordReissueService.resetFailure(form.getUsername(), form.getToken());
			model.addAttribute(e.getResultMessages());
			return showPasswordResetForm(form, model, form.getUsername(), form.getToken());
		}
	}
	
	@RequestMapping(value="resetpassword", params="complete")
	public String resetPasswordComplete(){
		return "passwordreissue/passwordResetComplete";
	}
	
	@ModelAttribute("createTokenForm")
	public CreateTokenForm setupReissueForm(){
		return new CreateTokenForm();
	}
	
	@ModelAttribute("passwordResetForm")
	public PasswordResetForm setupResetForm(){
		return new PasswordResetForm();
	}
}
