package com.example.auth_service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController  // Tells Spring: this class handles HTTP requests
@CrossOrigin     // Allows Flutter to call this from a different origin
public class AuthController {

  @Autowired
  private UserRepository userRepository; // Person 3's database access layer

  @Autowired
  private JwtUtil jwtUtil; // Person 4's JWT generator

  @Autowired
  private PasswordEncoder passwordEncoder; // For secure password comparison

  // Handles: POST /login
  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequest request) {

    // Step 1: Look up user by username in the database
    User user = userRepository.findByUsername(request.getUsername());

    // Step 2: If user not found OR password doesn't match → return error
    if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
      return ResponseEntity
          .status(HttpStatus.UNAUTHORIZED) // HTTP 401
          .body(Map.of("message", "Invalid credentials"));
    }

    // Step 3: Passwords match → generate a JWT token
    String token = jwtUtil.generateToken(user.getUsername());

    // Step 4: Send the token back to Flutter
    return ResponseEntity.ok(Map.of("token", token));
  }
}
