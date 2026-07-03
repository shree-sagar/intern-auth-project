package com.example.auth_service;

import lombok.Data;

// @Data automatically generates getUsername() and getPassword()
// so you don't have to write them yourself
@Data
public class LoginRequest {
  private String username;
  private String password;
}
