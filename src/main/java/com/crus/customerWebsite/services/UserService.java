package com.crus.customerWebsite.services;

import com.crus.customerWebsite.models.Role;
import com.crus.customerWebsite.models.User;
import com.crus.customerWebsite.repos.RoleRepository;
import com.crus.customerWebsite.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserService implements UserDetailsService {
    
    @Autowired
    UserRepository userRepository;
    
    @Autowired
    PasswordEncoder encoder;

    @Autowired
    RoleRepository roleRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User originalUser = userRepository.findByUsername(username);

        if (originalUser == null) {
            throw new UsernameNotFoundException("User not found");
        }

        return originalUser;
    }

    public User registerUser(User userDetails) {

        Role userRole = roleRepository.findByRole(Role.Roles.ROLE_USER);
        if (userRole == null) {
            throw new IllegalStateException("User role not found");
        }

        userDetails.setId(null);
        userDetails.getAuthorities().forEach(a -> a.setId(null));

        userDetails.setAccountNonExpired(true);
        userDetails.setAccountNonLocked(true);
        userDetails.setCredentialsNonExpired(true);
        userDetails.setEnabled(true);
        userDetails.setAuthorities(
                Collections.singletonList(
                        new Role(Role.Roles.ROLE_USER)
                )
        );

        checkPassword(userDetails.getPassword());
        userDetails.setPassword(encoder.encode(userDetails.getPassword()));

        try {
            return userRepository.save(userDetails);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e.getCause());
        }
    }

    private void checkPassword(String password) {
        if (password == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }
        if (password.length() < 8) {
            throw new IllegalStateException("Password must be at least 8 characters");
        }
    }
}
