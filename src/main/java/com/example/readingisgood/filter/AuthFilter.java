package com.example.readingisgood.filter;

import com.example.readingisgood.factory.UserAuthenticationFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthFilter extends OncePerRequestFilter {

    private UserAuthenticationFactory userAuthenticationFactory;

    public AuthFilter(UserAuthenticationFactory userAuthenticationFactory) {
        this.userAuthenticationFactory = userAuthenticationFactory;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        SecurityContextHolder.getContext().setAuthentication(
                userAuthenticationFactory.getAuthentication(request)
        );
        filterChain.doFilter(request, response);
    }
}
