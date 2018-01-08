package com.tmcl.siem.apis;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tmcl.siem.configuration.CustomAuthenticationProvider;
import com.tmcl.siem.domain.UserDetails;
import com.tmcl.siem.exception.PasswordMathcerExpection;
import com.tmcl.siem.listener.OnRegistrationCompleteEvent;
import com.tmcl.siem.listener.OnUserCreateionEvent;
import com.tmcl.siem.listener.UserCreationEventPublisher;
import com.tmcl.siem.model.UserModel;
import com.tmcl.siem.service.UserService;
import com.tmcl.siem.util.SiEMUtil;

@Controller
@RequestMapping(value="/user")
public class UserController {



	@Autowired
	private ApplicationEventPublisher eventPublisher;
	
	@Autowired
	private UserCreationEventPublisher userCreatioEventPublisher;

	@Autowired
	private CustomAuthenticationProvider customAuthenticatioProvider;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UserService userService;


	@RequestMapping(value="/get-user-informaiton",method=RequestMethod.GET)
	protected @ResponseBody Map<String, Object> getUserInformation() {
		Map<String, Object> dataMap = Maps.newHashMap();
		List<UserDetails> userDetails = userService.findByUserName(SiEMUtil.getCurrentLoginedUser());
		if(!userDetails.isEmpty()) {
			dataMap.put("planName", userDetails.get(0).getPlanDetails().getPlanName());
			dataMap.put("companyName", userDetails.get(0).getCompanyName());
			dataMap.put("userName", userDetails.get(0).getUserName());
			dataMap.put("dailyIngest", SiEMUtil.formatFileSize(userDetails.get(0).getPlanDetails().getDailyIngest()));
			dataMap.put("retention", userDetails.get(0).getPlanDetails().getRetention());
			dataMap.put("users", userDetails.get(0).getPlanDetails().getMaxNumberOfUsers());
			dataMap.put("alerts", userDetails.get(0).getPlanDetails().getMaxDashboards());
			dataMap.put("accessToken", userDetails.get(0).getAccessToken());
			return dataMap;
		}
		return dataMap;
	}

	@RequestMapping(value="/getAllUsersWithinCompany",method=RequestMethod.GET)
	protected @ResponseBody List<Map<String, Object>> getAllUsersWithInCompany(@RequestParam(value="company") String companyName) {
		List<Map<String, Object>> userDetails = userService.findUsersByCompany(companyName);
		return userDetails;
	}
	
	@RequestMapping(value="/change_password",method=RequestMethod.GET)
	protected String changePassword() {
		return "change_password.html";
	}
	
	@RequestMapping(value="/createlocalusrers",method=RequestMethod.POST)
	protected @ResponseBody Map<String, Object> createLocalUsers(@RequestBody UserModel userModel){
		Map<String, Object> dataMap = Maps.newHashMap();
		
		try{
			String password = SiEMUtil.generateRandomPassword();
			userModel.setPassword(password);
			UserDetails userDetails =  userService.createLocalUsers(userModel);
			eventPublisher.publishEvent(new OnUserCreateionEvent(userDetails,password));
			dataMap.put("status", true);
			dataMap.put("users", userService.findUsersByCompany(userDetails.getCompanyName()));
		}catch (Exception e) {
			dataMap.put("status", false);
		}
		
		
		return dataMap;
		
	}



	@RequestMapping(value="/register",method=RequestMethod.POST)
	protected @ResponseBody Map<String, Object> registerUser(@RequestBody @Valid UserModel userModel,BindingResult bindingResult,final HttpServletRequest request) {

		Map<String, Object> dataMap = Maps.newHashMap();
		if(bindingResult.hasErrors()) {
			dataMap.put("validationErrors",bindingResult.getFieldErrors());
			dataMap.put("status",false);
			return dataMap;
		}
		try {
			List<UserDetails> existingUsers = userService.findByUserName(userModel.getUserName());
			if(!existingUsers.isEmpty()) {
				dataMap.put("status",false);
				dataMap.put("errorMessage","User Already Exist .. Please register with another email");
				return dataMap;
			}
			final UserDetails userDetails =  userService.saveUserDetails(userModel);
			eventPublisher.publishEvent(new OnRegistrationCompleteEvent(userDetails, request.getLocale(), getAppUrl(request)));
			dataMap.put("status",true);
		}catch (Exception e) {
			dataMap.put("errorMessage", e.getMessage());
			dataMap.put("status","error");
		}


		return dataMap;
	}

	@RequestMapping(value="/custom-login",method=RequestMethod.POST)
	protected @ResponseBody Map<String, String> login(@RequestBody Map<String, String> loginDetails,HttpServletRequest httpRequest){

		String username = loginDetails.get("username");
		String password = loginDetails.get("password");
		Map<String, String> resultMap = Maps.newHashMap();

		try {
			List<UserDetails> userDetails = userService.findByUserName(username);
			if(!userDetails.isEmpty() && passwordEncoder.matches(password, userDetails.get(0).getPassword())) {
				
				Authentication authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
				Authentication authentication = customAuthenticatioProvider
						.authenticate(authenticationToken);
				SecurityContext securityContext = SecurityContextHolder
						.getContext();

				securityContext.setAuthentication(authentication);

				HttpSession session = httpRequest.getSession(true);
				session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);
				if(userDetails.get(0).getStatus().equalsIgnoreCase("Password Reset")) {
					resultMap.put("requiredPasswordChange", "true");
				}else {
					resultMap.put("requiredPasswordChange", "false");
				}
				resultMap.put("token", userDetails.get(0).getAccessToken());
				
				return resultMap;
			}


		}catch (Exception e) {
			e.printStackTrace();
		}

		return resultMap;
	}

	@CrossOrigin(origins = "http://localhost:5601")
	@RequestMapping(value="/get-user-details",method=RequestMethod.GET)
	protected @ResponseBody Map<String, String> getUserDetails(@RequestParam(value="token") String token){
		UserDetails userDetails = 	userService.getUserDetailsBasedOnToken(token);
		Map<String, String> dataMap = Maps.newHashMap();
		if(userDetails!=null) {
			dataMap.put("userName", userDetails.getUserName());
			dataMap.put("password", userDetails.getElasticsearchPassword());
			dataMap.put("indexName", userDetails.getElasticsearchStorge().getElasticsearchIndexName());
		}

		return dataMap;

	}


	@RequestMapping(value="/register-confirm",method=RequestMethod.GET)
	protected void registrationConfirmation(@RequestParam(value="token") String token, HttpServletResponse httpServletResponse) throws IOException {
		String validationStatus = userService.validateEmail(token);
		if(validationStatus.equalsIgnoreCase("valid")) {
			httpServletResponse.sendRedirect("http://localhost:8999/registration-success.html");
		}
		if(validationStatus.equalsIgnoreCase("expried")) {
			httpServletResponse.sendRedirect("http://localhost:8999/token-expried.html");
		}
		if(validationStatus.equalsIgnoreCase("invalid")) {
			httpServletResponse.sendRedirect("http://localhost:8999/invalid-expried.html");
		}
	}

	@RequestMapping(value="/get-token",method=RequestMethod.GET)
	protected @ResponseBody Map<String, String> getAccessToken(){
		Map<String, String> dataMap = Maps.newHashMap();

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		List<UserDetails> userDetails = userService.findByUserName(authentication.getName());
		if(!userDetails.isEmpty()) {
			dataMap.put("token", userDetails.get(0).getAccessToken());
		}
		return dataMap;
	}

	@RequestMapping(value="/change-password",method=RequestMethod.POST)
	protected @ResponseBody Map<String, Object> changePassword(@RequestBody UserModel userModel){
		Map<String, Object> dataMap = Maps.newHashMap();

		try {
			userModel.setUserName(SiEMUtil.getCurrentLoginedUser());
			userService.changePassword(userModel);
			dataMap.put("status",true);
		}catch (UsernameNotFoundException | PasswordMathcerExpection e) {
			dataMap.put("status", false);
			dataMap.put("error", e.getMessage());
		}

		return dataMap;
	}
	@RequestMapping(value="/change-password-after-login",method=RequestMethod.POST)
	protected @ResponseBody Map<String, Object> changePasswordAfterLogin(@RequestBody UserModel userModel){
		Map<String, Object> dataMap = Maps.newHashMap();

		try {
			userModel.setUserName(SiEMUtil.getCurrentLoginedUser());
			userService.changePasswordAfterLogin(userModel);
			dataMap.put("status",true);
		}catch (UsernameNotFoundException | PasswordMathcerExpection e) {
			dataMap.put("status", false);
			dataMap.put("error", e.getMessage());
		}

		return dataMap;
	}
	
	

	private String getAppUrl(HttpServletRequest request) {
		return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
	}
}
