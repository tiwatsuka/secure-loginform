package com.example.security.app.unlock;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.terasoluna.gfw.common.exception.BusinessException;

import com.example.security.domain.service.account.AccountSharedService;

@Controller
@RequestMapping("/unlock")
public class UnlockController {

	@Inject
	AccountSharedService accountSharedService;
	
	@RequestMapping(params="form")
	public String create(UnlockForm form, Model model){
		return "unlock/unlockForm";
	}
	
	@RequestMapping(method=RequestMethod.POST)
	public String unlock(@Validated UnlockForm form, BindingResult bindingResult, Model model, RedirectAttributes attributes){
		if(bindingResult.hasErrors()){
			return create(form, model);
		}
		
		try {
			accountSharedService.unlock(form.getUsername());
			attributes.addFlashAttribute("username", form.getUsername());
			return "redirect:/unlock?complete&username=" + form.getUsername();
		} catch (BusinessException e) {
			model.addAttribute(e.getResultMessages());
			return create(form, model);
		}
	}
	
	@RequestMapping(method=RequestMethod.GET, params="complete")
	public String unlockFinish(){
		return "unlock/unlockFinish";
	}
	
}
