package com.example.security.app.unlock;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.security.domain.service.account.AccountSharedService;

@Controller
@RequestMapping("/unlock")
public class UnlockController {

	@Inject
	AccountSharedService accountSharedService;
	
	@RequestMapping(params="form")
	public String create(UnlockForm form){
		return "unlock/unlockForm";
	}
	
	@RequestMapping(method=RequestMethod.POST)
	public String unlock(@Validated UnlockForm form, BindingResult bindingResult, Model model){
		if(bindingResult.hasErrors()){
			model.addAttribute(form);
			return "unlock/unlockForm";
		}
		
		accountSharedService.unlock(form.getUsername());
		
		return "redirect:/unlock?complete&username=" + form.getUsername();
	}
	
	@RequestMapping(method=RequestMethod.GET, params="complete")
	public String unlockFinish(@RequestParam("username") String username, Model model){
		model.addAttribute("username", username);
		return "unlock/unlockFinish";
	}
	
}
