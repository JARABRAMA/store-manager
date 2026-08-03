package com.jarabrama.store_manager.authentication.usecases;

import com.jarabrama.store_manager.authentication.domain.SessionRepositoryPort;
import com.jarabrama.store_manager.authentication.domain.SystemUserRepositoryPort;
import com.jarabrama.store_manager.authentication.domain.exceptions.InvalidCredentialsException;
import com.jarabrama.store_manager.authentication.domain.model.AuthToken;
import com.jarabrama.store_manager.authentication.domain.model.AuthTokenType;
import com.jarabrama.store_manager.authentication.domain.model.SystemUser;
import com.jarabrama.store_manager.authentication.presentation.dto.LoginRequest;
import com.jarabrama.store_manager.authentication.presentation.dto.LoginResponse;
import com.jarabrama.store_manager.authentication.service.JwtService;
import com.jarabrama.store_manager.authentication.service.model.NewJwtTokenRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class LoginUseCase {
  private final SystemUserRepositoryPort userRepo;
  private final PasswordEncoder passwordEncoder;
  private final SessionRepositoryPort sessionRepo;
  private final JwtService jwtService;

  private static final Duration JWT_ACCESS_TOKEN_TIMEOUT = Duration.ofMinutes(5);
  private static final Duration JWT_NOT_TRUSTED_REFRESH_TOKEN_TIMEOUT = Duration.ofMinutes(15);
  private static final Duration JWT_TRUSTED_REFRESH_TOKEN_TIMEOUT = Duration.ofDays(5);

  public LoginResponse execute(LoginRequest request) {
    var user = getUserAndValidateCredentials(request);
    sessionRepo.revokeAllByUser(user.getId());

    var accessToken = generateAccessToken(user);

    var refreshTokenRequest = NewJwtTokenRequest.builder()
            .trustedDeviceId(request.trustedDevice())
    return null;
  }


  private AuthToken generateAccessToken(SystemUser user) {
    var newTokenRequest = NewJwtTokenRequest.builder()
            .tokenType(AuthTokenType.ACCESS)
            .expirationTimeout(JWT_ACCESS_TOKEN_TIMEOUT)
            .username(user.getUsername())
            .userRole(user.getRole())
            .build();

    var accessTokenHash = jwtService.generateToken(newTokenRequest);
    var accessToken = AuthToken.fromTokenClaims(jwtService.getClaimsFromToken(accessTokenHash));
    accessToken.setTokenHash(accessTokenHash);
    accessToken.setUserId(user.getId());
    return accessToken;
  }


  private SystemUser getUserAndValidateCredentials(LoginRequest request) {
    var user = userRepo.findByUsername(request.username())
            .orElseThrow(() -> new InvalidCredentialsException("Credenciales incorrectas"));

    var correctPassword = passwordEncoder.matches(request.password(),
            user.getPasswordHash());

    if (!correctPassword) {
      throw new InvalidCredentialsException("Credenciales incorrectas");
    }
    return user;
  }
}
