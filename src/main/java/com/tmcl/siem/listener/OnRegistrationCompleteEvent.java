package com.tmcl.siem.listener;

import java.util.Locale;

import org.springframework.context.ApplicationEvent;

import com.tmcl.siem.domain.UserDetails;

public class OnRegistrationCompleteEvent extends ApplicationEvent{

	private final String appUrl;
	private final Locale locale;
	private final UserDetails user;
	
	public OnRegistrationCompleteEvent(final UserDetails user, final Locale locale, final String appUrl) {
        super(user);
        this.user = user;
        this.locale = locale;
        this.appUrl = appUrl;
    }

 

    public String getAppUrl() {
        return appUrl;
    }

    public Locale getLocale() {
        return locale;
    }

    public UserDetails getUser() {
        return user;
    }

}
