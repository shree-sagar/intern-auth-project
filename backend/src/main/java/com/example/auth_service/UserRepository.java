package com.example.auth_service;

import org.springframework.data.jpa.repository.JpaRepository;

// JpaRepository gives you save(), findById(), findAll(), delete() for free.
// You just add the custom query you need.
public interface UserRepository extends JpaRepository<User, Long> {

  // Spring generates: SELECT * FROM users WHERE username = ?
  User findByUsername(String username);
}