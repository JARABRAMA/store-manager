package com.jarabrama.store_manager.authentication.usecases;

import com.jarabrama.store_manager.authentication.domain.SystemUserRepositoryPort;
import com.jarabrama.store_manager.authentication.domain.exceptions.AuthException;
import com.jarabrama.store_manager.authentication.domain.model.AuthToken;
import com.jarabrama.store_manager.authentication.domain.model.AuthTokenType;
import com.jarabrama.store_manager.authentication.domain.model.SystemRole;
import com.jarabrama.store_manager.authentication.domain.model.SystemUser;
import com.jarabrama.store_manager.authentication.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class RefreshTokenUseCaseTest {

  @Mock
  private JwtService jwtService;

  @Mock
  private SystemUserRepositoryPort userRepo;

  @Mock
  private CreateNewAccessTokenUseCase createNewAccessTokenUseCase;

  @InjectMocks
  private RefreshTokenUseCase refreshTokenUseCase;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  private AuthToken refreshToken;
  private AuthToken accessToken;
  private SystemUser user;

  private void buildRefreshToken() {
    this.refreshToken = AuthToken.builder()
            .id(UUID.randomUUID())
            .userId(UUID.randomUUID())
            .tokenType(AuthTokenType.REFRESH)
            .tokenHash("tokenHash")
            .expiresAt(Instant.now().plus(Duration.ofMinutes(5)))
            .createdAt(Instant.now())
            .revoked(false)
            .build();
  }

  private void buildAccessToken() {
    this.accessToken = AuthToken.builder()
            .id(UUID.randomUUID())
            .userId(UUID.randomUUID())
            .tokenType(AuthTokenType.ACCESS)
            .tokenHash("tokenHash")
            .expiresAt(Instant.now().plus(Duration.ofMinutes(5)))
            .createdAt(Instant.now())
            .revoked(false)
            .build();
  }

  private void buildUser() {
    this.user = SystemUser.builder()
            .username("admin")
            .passwordHash("admin")
            .role(SystemRole.EMPLOYEE)
            .build();
  }

  @Test
  @DisplayName("When user sends a token that is not REFRESH token should throw AuthException")
  void when_user_sends_no_refresh_token_should_throw_AuthException() {
    buildAccessToken();
    var ex = assertThrows(AuthException.class, () -> refreshTokenUseCase.execute(accessToken));
    assertEquals("El token proporcionado no es un token de actualización válido", ex.getMessage());
  }

  @Test
  @DisplayName("When token is not signed with secret key should trow AuthException")
  void when_token_is_not_signed_should_trows_exception() {
    buildRefreshToken();
    when(jwtService.isSinged(refreshToken.getTokenHash())).thenReturn(false);

    var ex = assertThrows(AuthException.class, () -> refreshTokenUseCase.execute(refreshToken));
    assertEquals("Tu sesión no es válida. Por favor, inicia sesión nuevamente", ex.getMessage());
  }

  @Test
  @DisplayName("When token is expired timeout then throws AuthException")
  void when_token_is_expired_then_throws_exception() {
    buildRefreshToken();
    var token = this.refreshToken;
    token.setExpiresAt(Instant.now().minus(Duration.ofMinutes(35)));
    when(jwtService.isSinged(token.getTokenHash())).thenReturn(true);
    var ex = assertThrows(AuthException.class, () -> refreshTokenUseCase.execute(token));
    assertEquals("Tu sesión ya no es válida. Por favor, inicia sesión nuevamente", ex.getMessage());
  }

  @Test
  @DisplayName("When token is revoked then throws AuthException")
  void when_token_is_revoked_then_throws_exception() {
    buildRefreshToken();
    var token = this.refreshToken;
    token.setRevoked(true);
    when(jwtService.isSinged(token.getTokenHash())).thenReturn(true);
    var ex = assertThrows(AuthException.class, () -> refreshTokenUseCase.execute(token));
    assertEquals("Tu sesión ya no es válida. Por favor, inicia sesión nuevamente", ex.getMessage());
  }

  @Test
  @DisplayName("when token is valid but does not belong to any user then throws AuthenticationException")
  void when_token_does_not_belong_to_any_user_then_throws_exception() {
    buildRefreshToken();
    when(jwtService.isSinged(refreshToken.getTokenHash())).thenReturn(true);
    when(userRepo.findById(this.refreshToken.getUserId())).thenReturn(Optional.empty());

    var ex = assertThrows(AuthException.class, () -> refreshTokenUseCase.execute(refreshToken));
    assertEquals("Tu sesión no es válida. Por favor, inicia sesión nuevamente", ex.getMessage());
  }

  @Test
  @DisplayName("When token is valid and belong to a user then generate new access token for that user and return it")
  void when_valid_toke_generate_new_access_token_for_that_user_and_return_it() {
    buildRefreshToken();
    buildAccessToken();
    buildUser();
    when(jwtService.isSinged(refreshToken.getTokenHash())).thenReturn(true);
    when(userRepo.findById(this.refreshToken.getUserId())).thenReturn(Optional.of(this.user));
    when(createNewAccessTokenUseCase.execute(this.user)).thenReturn(this.accessToken);

    var actual = this.refreshTokenUseCase.execute(refreshToken);
    assertEquals(this.refreshToken.getTokenHash(), actual.getTokenHash());
  }


}