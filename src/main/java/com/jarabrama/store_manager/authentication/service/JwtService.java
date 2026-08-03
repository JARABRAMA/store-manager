package com.jarabrama.store_manager.authentication.service;

import com.jarabrama.store_manager.authentication.domain.model.AuthToken;
import com.jarabrama.store_manager.authentication.service.model.NewJwtTokenRequest;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;

@Component
public class JwtService {

  @Value("${jwt.secret}")
  private String secretKey;

  public String generateToken(NewJwtTokenRequest req) {
    var expirationDate = Date.from(Instant.now().plus(req.expirationTimeout()));
    var extraClaims = getClaimsFromNewJwtTokenRequest(req);

    return Jwts.builder()
            .claims(extraClaims)
            .subject(req.username())
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(expirationDate)
            .signWith(getSignInKey())
            .compact();
  }

  private HashMap<String, String> getClaimsFromNewJwtTokenRequest(NewJwtTokenRequest req) {
    var extraClaims = new HashMap<String, String>();
    extraClaims.put("userRole", req.userRole().toString());
    extraClaims.put("sessionId", req.sessionId() != null ? req.sessionId().toString() : null);
    extraClaims.put("trustedDeviceId", req.trustedDeviceId() != null ? req.trustedDeviceId().toString() : null);
    extraClaims.put("tokenType", req.tokenType().toString());
    return extraClaims;
  }

  private SecretKey getSignInKey() {
    var bites = Decoders.BASE64.decode(secretKey);
    return Keys.hmacShaKeyFor(bites);
  }

  public Claims getClaimsFromToken(String token) {
    return Jwts.parser()
            .verifyWith(getSignInKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
  }
}