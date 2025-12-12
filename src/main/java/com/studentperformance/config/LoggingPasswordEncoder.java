package com.studentperformance.config;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class LoggingPasswordEncoder implements PasswordEncoder {

    private final BCryptPasswordEncoder delegate = new BCryptPasswordEncoder();

    @Override
    public String encode(CharSequence rawPassword) {
        System.out.println("=== ENCODING PASSWORD ===");
        System.out.println("Raw: " + rawPassword);
        String encoded = delegate.encode(rawPassword);
        System.out.println("Encoded: " + encoded);
        return encoded;
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        System.out.println("=== PASSWORD MATCH CHECK ===");
        System.out.println("Raw password from form: '" + rawPassword + "'");
        System.out.println("Encoded password from DB: '" + encodedPassword + "'");
        System.out.println("DB password length: " + encodedPassword.length());
        System.out.println("Starts with $2a$? " + encodedPassword.startsWith("$2a$"));

        boolean result = delegate.matches(rawPassword, encodedPassword);
        System.out.println("MATCH RESULT: " + (result ? "✓ PASS" : "✗ FAIL"));

        if (!result) {
            System.out.println("=== DEBUG MISMATCH ===");
            // Generate a new hash with the same password to compare
            String testHash = delegate.encode(rawPassword);
            System.out.println("Test hash of raw password: " + testHash);
            System.out.println("First 30 chars DB:   " + encodedPassword.substring(0, Math.min(30, encodedPassword.length())));
            System.out.println("First 30 chars Test: " + testHash.substring(0, Math.min(30, testHash.length())));
        }

        return result;
    }
}