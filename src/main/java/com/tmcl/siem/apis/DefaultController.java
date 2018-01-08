package com.tmcl.siem.apis;

import java.security.Principal;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DefaultController {

	@RequestMapping(value="/")
	protected String  defaultController(Principal p,HttpServletResponse response) {
		if(p==null) {
			return "home.html";
		}else {
			return "redirect:/index.html#!/kibana";
		}
	}
}
