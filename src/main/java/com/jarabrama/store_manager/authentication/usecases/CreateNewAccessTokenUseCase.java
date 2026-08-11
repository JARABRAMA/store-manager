package com.jarabrama.store_manager.authentication.usecases;

import com.jarabrama.store_manager.authentication.domain.AuthTokenRepositoryPort;
import com.jarabrama.store_manager.authentication.domain.model.AuthToken;
import com.jarabrama.store_manager.authentication.domain.model.AuthTokenType;
import com.jarabrama.store_manager.authentication.domain.model.SystemUser;
import com.jarabrama.store_manager.authentication.service.JwtService;
import com.jarabrama.store_manager.authentication.service.model.NewJwtTokenRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class CreateNewAccessTokenUseCase {
  private final static Duration EXPIRATION_TIME = Duration.ofMinutes(5);

  private final JwtService jwtService;
  private final AuthTokenRepositoryPort authTokenRepo;

  public AuthToken execute(SystemUser user) {
    var now = Instant.now();
    var newToken = AuthToken.builder()
            .userId(user.getId())
            .tokenType(AuthTokenType.ACCESS)
            .createdAt(now)
            .expiresAt(now.plus(EXPIRATION_TIME))
            .revoked(false)
            .build();

    var newTokenHashRequest = NewJwtTokenRequest.builder()
            .username(user.getUsername())
            .userRole(user.getRole())
            .tokenType(AuthTokenType.ACCESS)
            .expirationTime(now.plus(EXPIRATION_TIME))
            .build();

    newToken.setTokenHash(jwtService.generateToken(newTokenHashRequest));


    return authTokenRepo.save(newToken);
  }
}
