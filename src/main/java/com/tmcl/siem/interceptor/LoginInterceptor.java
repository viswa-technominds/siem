package com.tmcl.siem.interceptor;


import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.tmcl.siem.domain.UserDetails;
import com.tmcl.siem.service.UserService;
import com.tmcl.siem.util.SiEMUtil;

@Component
public class LoginInterceptor extends HandlerInterceptorAdapter {

	@Autowired
	private UserService userService; 
	
	@Override
	 public boolean preHandle(HttpServletRequest request,  HttpServletResponse response, Object object) throws Exception {
		
		String userName = SiEMUtil.getCurrentLoginedUser();
		System.out.println("username==>>"+userName);
		
		
		return true;
		
	 }
	

}
