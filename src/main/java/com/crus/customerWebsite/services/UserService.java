package com.crus.customerWebsite.services;

import com.crus.customerWebsite.models.Customer;
import com.crus.customerWebsite.models.Role;
import com.crus.customerWebsite.models.User;
import com.crus.customerWebsite.repos.CustomerRepository;
import com.crus.customerWebsite.repos.RoleRepository;
import com.crus.customerWebsite.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class UserService implements UserDetailsService {
    
    @Autowired
    UserRepository userRepository;
    
    @Autowired
    PasswordEncoder encoder;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found " + username);
        }

        return user;
    }

    public User registerUser(User userDetails) {

        try {
            if (userRepository.findByUsername(userDetails.getUsername()) != null) {
                throw new IllegalStateException("User already exists");
            }

        Role userRole = roleRepository.findByRole(Role.Roles.ROLE_USER);
        if (userRole == null) {
            throw new IllegalStateException("User role not found");
        }

        userDetails.setId(null);

        if (userDetails.getAuthorities() != null) {
            userDetails.getAuthorities().clear();
        }

        userDetails.setAccountNonExpired(true);
        userDetails.setAccountNonLocked(true);
        userDetails.setCredentialsNonExpired(true);
        userDetails.setEnabled(true);

        userDetails.setAuthorities(
                Collections.singletonList(userRole));

       // userDetails.getAuthorities().forEach(a -> a.setId(null));

            if (userDetails.getCustomer() == null) {
                Customer customer = new Customer();
                Customer savedCustomer = customerRepository.save(customer);
                userDetails.setCustomer(customer);
            }


        checkPassword(userDetails.getPassword());
        userDetails.setPassword(encoder.encode(userDetails.getPassword()));

//        try {
//            return userRepository.save(userDetails);
//        } catch (Exception e) {
//            throw new IllegalStateException(e.getMessage(), e.getCause());
//        }
    User savedUser = userRepository.save(userDetails);
            System.out.println("User saved with ID : " + savedUser.getId());

            return savedUser;
        } catch (Exception e) {
            System.out.println("Error saving user: " + e.getMessage());
            e.printStackTrace();
            throw new IllegalStateException("Failed to register user: " + e.getMessage(), e);
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

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
