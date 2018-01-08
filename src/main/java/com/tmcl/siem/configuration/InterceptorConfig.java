package com.tmcl.siem.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.tmcl.siem.interceptor.LoginInterceptor;

@Component
public class InterceptorConfig extends WebMvcConfigurerAdapter {

	@Autowired
	LoginInterceptor requestInterceptor;
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(requestInterceptor);
	}
}
