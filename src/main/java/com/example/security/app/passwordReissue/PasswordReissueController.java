package com.example.security.app.passwordReissue;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
		String rowPassword = info.getPassword();

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
	
	@ModelAttribute
	public PasswordReissueForm setupReissueForm(){
		return new PasswordReissueForm();
	}
}
