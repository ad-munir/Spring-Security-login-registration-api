package com.spring.authentification.controller;

import com.spring.authentification.dto.LoginDto;
import com.spring.authentification.dto.RegisterDto;
import com.spring.authentification.entity.Role;
import com.spring.authentification.entity.User;
import com.spring.authentification.repository.RoleRepository;
import com.spring.authentification.repository.UserRepository;
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
    public ResponseEntity<String> authenticateUser(@RequestBody LoginDto loginDto) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return new ResponseEntity<>("User login successfully!...", HttpStatus.OK);
    }


    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterDto registerDto){
        // checking for username exists in a database
        if(userRepository.existsByUsername(registerDto.getUsername())){
            return new ResponseEntity<>("Username is already taken!", HttpStatus.BAD_REQUEST);
        }
        // checking for email exists in a database
        if(userRepository.existsByEmail(registerDto.getEmail())){
            return new ResponseEntity<>("Email is already taken!", HttpStatus.BAD_REQUEST);
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
        return new ResponseEntity<>("User is registered successfully!", HttpStatus.OK);
    }



    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUsekr(@RequestBody LoginDto loginDto) {
        try {
            // Authenticate the user using Spring Security's AuthenticationManager
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginDto.getUsernameOrEmail(), loginDto.getPassword()));

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

    // Endpoint for user registration (sign-up)
    @PostMapping("/signup")
    public ResponseEntity<> registerUser(@RequestBody SignUpDto signUpDto) {

        try {
            // Check if the username is already taken
            if (userRepository.existsByUsername(signUpDto.getUsername())) {
                ApiResponse response = new ApiResponse("Username is already taken.", new Date(), 400);
                return ResponseEntity.badRequest().body(response);
            }

            // Check if the email is already taken
            if (userRepository.existsByEmail(signUpDto.getEmail())) {
                ApiResponse response = new ApiResponse("Email is already taken.", new Date(), 400);
                return ResponseEntity.badRequest().body(response);
            }

            // Create a new user object and set its properties
            User user = new User();
            user.setFirstname(signUpDto.getFirstname());
            user.setLastname(signUpDto.getLastname());
            user.setUsername(signUpDto.getUsername());
            user.setEmail(signUpDto.getEmail());
            user.setPassword(passwordEncoder.encode(signUpDto.getPassword()));

            // Get the "ADMIN" role and assign it to the user
            Role roles = roleRepository.findByName("SEEKER").get();
            user.setRoles(Collections.singleton(roles));

            // Save the user to the database
            userRepository.save(user);

            // Return a success response
            ApiResponse response = new ApiResponse("User registered successfully!", new Date(), 200);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            ApiResponse response = new ApiResponse("Registration failed.", new Date(), 400);
            return ResponseEntity.badRequest().body(response);
        }
}
