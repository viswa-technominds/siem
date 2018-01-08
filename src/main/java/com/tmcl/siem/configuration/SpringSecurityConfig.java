package com.tmcl.siem.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity

public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
    private IACAccessDeniedHandler accessDeniedHandler;
	
   @Autowired
   private CustomAuthenticationProvider customAuthenticationProvider;

  
  
    @Override
    protected void configure(HttpSecurity http) throws Exception {

      http.csrf().disable()
                .authorizeRequests()
					.antMatchers("/","/user/register/**","/user/get-user-details/**", "/user/custom-login/**", "/assets/**","/css/**","/js/**","/lib/**","/plugins/**", "/register.html","/login.html","/token-expried.html","/invalid-expried.html","/user/register-confirm/**","/registration-success.html","/rest/**","/user/email/**","/topic/greetings","/app/hello","/favicon.ico").permitAll()
					.antMatchers("/admin/**","index.html").hasAnyRole("ADMIN")
					.antMatchers("/user/**","/index.html").hasAnyRole("USER")
					.anyRequest().authenticated()
                .and()
                .formLogin()
					.loginPage("/login").defaultSuccessUrl("/home.html#!/incidents")
					.permitAll()
					.and()
                .logout().logoutUrl("/logout").logoutSuccessUrl("/").deleteCookies("JSESSIONID")
					.permitAll().and()
	                .exceptionHandling().accessDeniedHandler(accessDeniedHandler);
				
    }
    @Override
    public void configure(WebSecurity web) throws Exception {
      web
        .ignoring()
           .antMatchers("/resources/**"); 
    }
    
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    	
     	
    	 auth.authenticationProvider(customAuthenticationProvider);
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
}