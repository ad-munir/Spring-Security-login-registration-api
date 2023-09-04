package com.spring.authentification.controller;

import com.spring.authentification.dto.LoginDto;
import com.spring.authentification.dto.RegisterDto;
import com.spring.authentification.entity.Role;
import com.spring.authentification.entity.User;
import com.spring.authentification.repository.RoleRepository;
import com.spring.authentification.repository.UserRepository;
import com.spring.authentification.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Date;

@RestController
@CrossOrigin
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;


    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginDto loginDto) {

        try {
            // Authenticate the user using Spring Security's AuthenticationManager
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));

            // Set the authenticated user in the SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Return a success response
            ApiResponse response = new ApiResponse("User signed-in successfully!", new Date(), 200);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse("Invalid username or password.", new Date(), 400);
            return ResponseEntity.badRequest().body(response);
        }
    }


    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterDto registerDto) {
        try {
            // checking for username exists in a database
            if (userRepository.existsByUsername(registerDto.getUsername())) {
                ApiResponse response = new ApiResponse("Username is already taken.", new Date(), 400);
                return ResponseEntity.badRequest().body(response);
            }
            // checking for email exists in a database
            if (userRepository.existsByEmail(registerDto.getEmail())) {
                ApiResponse response = new ApiResponse("Email is already taken.", new Date(), 400);
                return ResponseEntity.badRequest().body(response);
            }
            // creating user object
            User user = new User();
            user.setName(registerDto.getName());
            user.setUsername(registerDto.getUsername());
            user.setEmail(registerDto.getEmail());
            user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
            Role roles = roleRepository.findByName("ADMIN").get();
            user.setRoles(Collections.singleton(roles));
            userRepository.save(user);

            // Return a success response
            ApiResponse response = new ApiResponse("User registered successfully!", new Date(), 200);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            ApiResponse response = new ApiResponse("Registration failed.", new Date(), 400);
            return ResponseEntity.badRequest().body(response);
        }
    }
}