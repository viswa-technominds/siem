package com.tmcl.siem.listener;

import java.util.UUID;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import com.tmcl.siem.domain.UserDetails;
import com.tmcl.siem.service.UserService;

@Component
public class RegistrationEventPublisher implements ApplicationListener<OnRegistrationCompleteEvent>{

	@Autowired
	private UserService userService;

	@Autowired
	private MessageSource messages;

	private static final String HOST = "smtp.gmail.com";
	private static final int PORT = 465;
	private static final boolean SSL_FLAG = true; 


	@Override
	public void onApplicationEvent(final OnRegistrationCompleteEvent event) {
		try {
			this.confirmRegistration(event);
		} catch (EmailException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void confirmRegistration(final OnRegistrationCompleteEvent event) throws EmailException {
		final UserDetails user = event.getUser();
		final String token = UUID.randomUUID().toString();
		userService.createVerificationTokenForUser(user, token);

		constructEmailMessage(event, user, token);
		//mailSender.send(email);
	}

	private final void constructEmailMessage(final OnRegistrationCompleteEvent event, final UserDetails user, final String token) throws EmailException {
		final String recipientAddress = user.getUserName();
		final String subject = "Registration Confirmation";
		final String confirmationUrl = event.getAppUrl() + "/user/register-confirm?token=" + token;
		
		String userName = "prasanth.viswanatham@gmail.com";
		String password = "prasanth@12345";

		String fromAddress="prasanth.viswanatham@gmail.com";
		

		Email email = new SimpleEmail();
		email.setHostName(HOST);
		email.setSmtpPort(PORT);
		email.setAuthenticator(new DefaultAuthenticator(userName, password));
		email.setSSLOnConnect(SSL_FLAG);
		email.setFrom(fromAddress);
		email.setSubject(subject);
		email.setMsg(confirmationUrl);
		email.addTo(recipientAddress);
		email.send();
	}

}
