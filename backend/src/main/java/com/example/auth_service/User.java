package com.example.auth_service;

import jakarta.persistence.*;
import lombok.Data;

@Entity             // Tells Spring: this class maps to a database table
@Table(name = "users")
@Data               // Lombok: auto-generates getters and setters
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment ID
  private Long id;

  @Column(unique = true, nullable = false)
  private String username;

  @Column(nullable = false)
  private String password; // Stored as BCrypt hash
}