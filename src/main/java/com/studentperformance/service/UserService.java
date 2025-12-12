package com.studentperformance.service;

import com.studentperformance.model.domain.User;
import com.studentperformance.model.enums.Role;
import com.studentperformance.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService implements UserDetailsService {
    
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        if (!user.isEnabled()) {
            throw new UsernameNotFoundException("User is disabled: " + username);
        }

        // Create Spring Security UserDetails object
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.isEnabled(),
                true,  // account non-expired
                true,  // credentials non-expired
                true,  // account non-locked
                getGrantedAuthorities(user.getRole())
        );
    }

    private List<GrantedAuthority> getGrantedAuthorities(String role) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        // Convert role string to Spring Security authority
        if (role != null && !role.isEmpty()) {
            // Add ROLE_ prefix for Spring Security
            String authority = "ROLE_" + role.toUpperCase();
            authorities.add(new SimpleGrantedAuthority(authority));
        }

        return authorities;
    }

    
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public long getAllUsersCount() {
        return userRepository.count();
    }
    
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}