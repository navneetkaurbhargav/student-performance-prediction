package com.studentperformance.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Map URLs to view templates
        registry.addViewController("/").setViewName("home");
        registry.addViewController("/login").setViewName("login");
        
        // Student routes
        registry.addViewController("/student").setViewName("student/dashboard");
        
        // Faculty routes
        registry.addViewController("/faculty").setViewName("faculty/dashboard");
        
        // Admin routes
        registry.addViewController("/admin").setViewName("admin/dashboard");

        registry.addViewController("/student/predict-all-results")
                .setViewName("student/predict-all-results");
    }
}