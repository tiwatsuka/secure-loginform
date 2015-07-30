package com.example.security.app.reissue;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/reissue")
public class ReissueController {
	@RequestMapping(value = "create", params = "form")	//modified
	public String showForm(ReissueForm form, Model model){
		return "reissue/reissueForm";
	}
	
	@ModelAttribute
	public ReissueForm setupReissueForm(){	//modified
		return new ReissueForm();
	}
}
