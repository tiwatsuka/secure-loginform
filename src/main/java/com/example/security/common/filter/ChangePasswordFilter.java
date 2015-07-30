package com.example.security.common.filter;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.security.domain.service.userdetails.SampleUserDetails;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ChangePasswordFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
    	Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();

    	if(!httpServletRequest.getServletPath().equals("/password")&&authentication != null){
    		Object principal = authentication.getPrincipal();
    		if(principal instanceof UserDetails){
    			SampleUserDetails userDetails = (SampleUserDetails)principal;
    			if(userDetails.isPasswordExpired() || userDetails.isInitialPassword()){
    				httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + "/password?form");
    			}
    		}
    	}
    	filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
