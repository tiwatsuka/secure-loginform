package com.example.security.app.interceptor;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.example.security.domain.model.Role;
import com.example.security.domain.service.userdetails.SampleUserDetails;

public class ChangePasswordInterceptor extends HandlerInterceptorAdapter {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException{
		Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();

    	if(authentication != null){
    		Object principal = authentication.getPrincipal();
    		if(principal instanceof UserDetails){
    			SampleUserDetails userDetails = (SampleUserDetails)principal;
    			if( (userDetails.isPasswordExpired() && userDetails.getAccount().getRole() == Role.ADMN)
    					|| userDetails.isInitialPassword()){
    				response.sendRedirect(request.getContextPath() + "/password?form");
    				return false;
    			}
    		}
    	}
    	
    	return true;
	}
}
