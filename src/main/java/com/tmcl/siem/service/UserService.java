package com.tmcl.siem.service;

import java.util.List;
import java.util.Map;

import com.tmcl.siem.domain.UserDetails;
import com.tmcl.siem.exception.PasswordMathcerExpection;
import com.tmcl.siem.model.UserModel;

public interface UserService {

	UserDetails saveUserDetails(UserModel userDetails);

	void createVerificationTokenForUser(UserDetails user, String token);

	String validateEmail(String token);
	
	List<UserDetails> findByUserName(String userName);
	
	boolean createElasticSearchIndex(String indexname);
	
	UserDetails getUserDetailsBasedOnToken(String token);

	void changePassword(UserModel userModel)throws PasswordMathcerExpection;

	List<Map<String, Object>> findUsersByCompany(String currentLoginedUser);

	UserDetails createLocalUsers(UserModel userModel);

	void changePasswordAfterLogin(UserModel userModel) throws PasswordMathcerExpection;

	
	
	
}
