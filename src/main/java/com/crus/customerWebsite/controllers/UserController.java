package com.crus.customerWebsite.controllers;

import com.crus.customerWebsite.models.User;
import com.crus.customerWebsite.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public User showRegistrationForm(Authentication authentication) {
        return (User) authentication.getPrincipal();
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User userDetails) {
        try {
            return ResponseEntity.ok(
                    userService.registerUser(userDetails));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
}
