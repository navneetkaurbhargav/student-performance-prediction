package com.studentperformance.controller;

import com.studentperformance.model.domain.User;
import com.studentperformance.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    @Autowired
    private UserService userDetailsService;

    @GetMapping("/")
    public String home(Model model,
                       @RequestParam(value = "error", required = false) String error,
                       @RequestParam(value = "logout", required = false) String logout) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Check if user is authenticated
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            // Get user from database
            User user = userDetailsService.getUserByUsername(auth.getName());

            if (user != null) {
                // Redirect based on role from database
                String role = user.getRole();

                if ("ADMIN".equalsIgnoreCase(role)) {
                    return "redirect:/admin/dashboard";
                } else if ("FACULTY".equalsIgnoreCase(role)) {
                    return "redirect:/faculty/dashboard";
                } else if ("STUDENT".equalsIgnoreCase(role)) {
                    return "redirect:/student/dashboard";
                }
            }
        }

        // Add error/logout messages if present
        if (error != null) {
            model.addAttribute("error", "Invalid username or password!");
        }
        if (logout != null) {
            model.addAttribute("message", "You have been logged out successfully.");
        }

        return "home";
    }

    @GetMapping("/login")
    public String login(Model model,
                        @RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout) {

        // If user is already logged in, redirect to appropriate dashboard
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            return "redirect:/";
        }

        if (error != null) {
            model.addAttribute("error", "Invalid username or password!");
        }
        if (logout != null) {
            model.addAttribute("message", "You have been logged out successfully.");
        }

        return "login";
    }

    @GetMapping("/access-denied")
    public String accessDenied(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            model.addAttribute("username", auth.getName());
            model.addAttribute("authorities", auth.getAuthorities());
        }
        return "access-denied";
    }
}