package com.studentperformance.config;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        System.out.println("=== AUTHENTICATION FAILED ===");
        System.out.println("Exception: " + exception.getClass().getName());
        System.out.println("Message: " + exception.getMessage());

        // Log specific exceptions
        if (exception instanceof BadCredentialsException) {
            System.out.println("Bad credentials - wrong username or password");
        } else if (exception instanceof DisabledException) {
            System.out.println("User account is disabled");
        } else if (exception instanceof LockedException) {
            System.out.println("User account is locked");
        } else if (exception instanceof CredentialsExpiredException) {
            System.out.println("User credentials have expired");
        }

        // Default behavior
        response.sendRedirect("/login?error=true");
    }
}