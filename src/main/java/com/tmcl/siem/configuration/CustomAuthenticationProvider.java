package com.tmcl.siem.configuration;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.tmcl.siem.domain.UserDetails;
import com.tmcl.siem.repo.UserRepo;


@Component
public class CustomAuthenticationProvider implements AuthenticationProvider{

	@Autowired
	private UserRepo usersRepo;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String userName = authentication.getName();
		try {
			List<UserDetails> userDetails = usersRepo.findByUserName(userName);
			if(!userDetails.isEmpty()) {
				List<GrantedAuthority> grantedAuths = new ArrayList<>();
				grantedAuths.add(new SimpleGrantedAuthority("ROLE_USER"));
				return new UsernamePasswordAuthenticationToken(userName, authentication.getCredentials().toString(),grantedAuths);
			}



		} catch (Exception e) {
			throw new AuthenticationCredentialsNotFoundException("Inavlid user");
		}

		throw new AuthenticationCredentialsNotFoundException("Inavlid user");


	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(
				UsernamePasswordAuthenticationToken.class);
	}

}