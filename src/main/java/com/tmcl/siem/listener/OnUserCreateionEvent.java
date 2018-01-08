package com.tmcl.siem.listener;



import org.springframework.context.ApplicationEvent;

import com.tmcl.siem.domain.UserDetails;

public class OnUserCreateionEvent  extends ApplicationEvent {

	

	
	private final UserDetails user;
	
	private final String password;
	
	
	public OnUserCreateionEvent(final UserDetails user,final String password) {
        super(user);
        this.user = user;
        this.password = password;
    }
  
    public UserDetails getUser() {
        return user;
    }

	public String getPassword() {
		return password;
	}

    
	
}
