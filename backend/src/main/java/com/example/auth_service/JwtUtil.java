package com.example.auth_service;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import java.util.Date;

@Component // Spring manages this class — anyone can @Autowired it
public class JwtUtil {
    
  @Value("${jwt.secret}") // Reads the secret from application.properties
  private String secretKeyString;

  // 30 minutes in milliseconds
  private static final long EXPIRATION_MS = 30 * 60 * 1000;

  // Converts the string secret to a proper cryptographic key
  private SecretKey getSigningKey() {
    return Keys.hmacShaKeyFor(secretKeyString.getBytes());
  }

  // ── GENERATE TOKEN ──────────────────────────────────────────────────────

  public String generateToken(String username) {
    Date now    = new Date();
    Date expiry = new Date(now.getTime() + EXPIRATION_MS);

    return Jwts.builder()
        .subject(username)          // "sub": who this token is for
        .issuedAt(now)              // "iat": when it was created
        .expiration(expiry)         // "exp": when it expires (30 min from now)
        .signWith(getSigningKey())  // Sign with our secret
        .compact();                 // Build the final token string
  }

  // ── VALIDATE TOKEN ──────────────────────────────────────────────────────

  // Returns true if token is valid and not expired
  public boolean validateToken(String token) {
    try {
      Jwts.parser()
          .verifyWith(getSigningKey())
          .build()
          .parseSignedClaims(token); // Throws exception if invalid or expired
      return true;
    } catch (JwtException e) {
      return false;
    }
  }

  // ── EXTRACT USERNAME FROM TOKEN ─────────────────────────────────────────

  public String getUsernameFromToken(String token) {
    return Jwts.parser()
        .verifyWith(getSigningKey())
        .build()
        .parseSignedClaims(token)
        .getPayload()
        .getSubject(); // Gets the username we stored in "sub"
  }
    
}
