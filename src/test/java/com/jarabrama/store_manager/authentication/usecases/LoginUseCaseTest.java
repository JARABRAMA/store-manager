package com.jarabrama.store_manager.authentication.usecases;

import com.jarabrama.store_manager.authentication.domain.SessionRepositoryPort;
import com.jarabrama.store_manager.authentication.domain.SystemUserRepositoryPort;
import com.jarabrama.store_manager.authentication.domain.exceptions.InvalidCredentialsException;
import com.jarabrama.store_manager.authentication.domain.model.AuthToken;
import com.jarabrama.store_manager.authentication.domain.model.AuthTokenType;
import com.jarabrama.store_manager.authentication.domain.model.SystemRole;
import com.jarabrama.store_manager.authentication.domain.model.SystemUser;
import com.jarabrama.store_manager.authentication.domain.model.TrustedDevice;
import com.jarabrama.store_manager.authentication.presentation.dto.LoginRequest;
import com.jarabrama.store_manager.authentication.presentation.dto.LoginResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginUseCaseTest {

  @Mock
  private SystemUserRepositoryPort userRepo;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private SessionRepositoryPort sessionRepo;

  @Mock
  private CreateNewAccessTokenUseCase createNewAccessTokenUseCase;

  @Mock
  private CreateRefreshTokenUseCase createRefreshTokenUseCase;

  @Mock
  private FindOrCreateTrustedDeviceUseCase findOrCreateTrustedDeviceUseCase;

  @Mock
  private CreateAndSaveNewSessionUseCase createAndSaveNewSessionUseCase;

  @InjectMocks
  private LoginUseCase loginUseCase;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    this.user = SystemUser.builder()
            .id(UUID.randomUUID())
            .username("admin")
            .passwordHash("password-hash")
            .role(SystemRole.EMPLOYEE)
            .build();

    when(userRepo.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
    when(passwordEncoder.matches(PASSWORD, user.getPasswordHash())).thenReturn(true);
  }

  private static final String PASSWORD = "admin123";

  private SystemUser user;

  private LoginRequest buildRequest(boolean trustedDevice) {
    return LoginRequest.builder()
            .username(user.getUsername())
            .password(PASSWORD)
            .deviceHash("device-hash")
            .trustedDevice(trustedDevice)
            .build();
  }

  private AuthToken buildToken(String hash, AuthTokenType type) {
    return AuthToken.builder()
            .tokenHash(hash)
            .tokenType(type)
            .userId(user.getId())
            .revoked(false)
            .build();
  }

  @Test
  @DisplayName("When user is not found should throw invalid credentials exception")
  void when_user_not_found_should_throw_invalid_credentials() {
    when(userRepo.findByUsername(user.getUsername())).thenReturn(Optional.empty());

    var ex = assertThrows(InvalidCredentialsException.class, () -> loginUseCase.execute(buildRequest(false)));
    assertEquals("Credenciales incorrectas", ex.getMessage());
  }

  @Test
  @DisplayName("When password does not match should throw invalid credentials exception")
  void when_password_does_not_match_should_throw_invalid_credentials() {
    when(passwordEncoder.matches(PASSWORD, user.getPasswordHash())).thenReturn(false);

    var ex = assertThrows(InvalidCredentialsException.class, () -> loginUseCase.execute(buildRequest(false)));
    assertEquals("Credenciales incorrectas", ex.getMessage());
  }

  @Test
  @DisplayName("When credentials are valid should revoke all sessions of the user")
  void verify_revoke_all_sessions_by_user() {
    when(createNewAccessTokenUseCase.execute(user)).thenReturn(buildToken("access-hash", AuthTokenType.ACCESS));
    when(createRefreshTokenUseCase.execute(eq(user), isNull())).thenReturn(buildToken("refresh-hash", AuthTokenType.REFRESH));

    loginUseCase.execute(buildRequest(false));

    verify(sessionRepo).revokeAllByUser(user.getId());
  }

  @Test
  @DisplayName("When device is not trusted should not create a trusted device and pass null id")
  void when_device_is_not_trusted_should_not_create_trusted_device() {
    var accessToken = buildToken("access-hash", AuthTokenType.ACCESS);
    var refreshToken = buildToken("refresh-hash", AuthTokenType.REFRESH);
    when(createNewAccessTokenUseCase.execute(user)).thenReturn(accessToken);
    when(createRefreshTokenUseCase.execute(eq(user), isNull())).thenReturn(refreshToken);

    loginUseCase.execute(buildRequest(false));

    verify(findOrCreateTrustedDeviceUseCase, never()).execute(any(), any());
    verify(createRefreshTokenUseCase).execute(user, null);
    verify(createAndSaveNewSessionUseCase).execute(user.getId(), null, refreshToken);
  }

  @Test
  @DisplayName("When device is trusted should find or create it and pass its id")
  void when_device_is_trusted_should_find_or_create_trusted_device() {
    var trustedDevice = TrustedDevice.builder()
            .id(UUID.randomUUID())
            .userId(user.getId())
            .deviceTokenHash("device-hash")
            .build();
    var accessToken = buildToken("access-hash", AuthTokenType.ACCESS);
    var refreshToken = buildToken("refresh-hash", AuthTokenType.REFRESH);
    when(findOrCreateTrustedDeviceUseCase.execute("device-hash", user.getId())).thenReturn(trustedDevice);
    when(createNewAccessTokenUseCase.execute(user)).thenReturn(accessToken);
    when(createRefreshTokenUseCase.execute(eq(user), eq(trustedDevice.getId()))).thenReturn(refreshToken);

    loginUseCase.execute(buildRequest(true));

    verify(findOrCreateTrustedDeviceUseCase).execute("device-hash", user.getId());
    verify(createRefreshTokenUseCase).execute(user, trustedDevice.getId());
    verify(createAndSaveNewSessionUseCase).execute(user.getId(), trustedDevice.getId(), refreshToken);
  }

  @Test
  @DisplayName("Verify that session is created and saved with the generated refresh token")
  void verify_session_is_created_with_the_refresh_token() {
    var accessToken = buildToken("access-hash", AuthTokenType.ACCESS);
    var refreshToken = buildToken("refresh-hash", AuthTokenType.REFRESH);
    when(createNewAccessTokenUseCase.execute(user)).thenReturn(accessToken);
    when(createRefreshTokenUseCase.execute(eq(user), isNull())).thenReturn(refreshToken);

    loginUseCase.execute(buildRequest(false));

    verify(createNewAccessTokenUseCase).execute(user);
    verify(createAndSaveNewSessionUseCase).execute(user.getId(), null, refreshToken);
  }

  @Test
  @DisplayName("Verify that response contains the access and refresh token hashes")
  void verify_response_contains_both_token_hashes() {
    var accessToken = buildToken("access-hash", AuthTokenType.ACCESS);
    var refreshToken = buildToken("refresh-hash", AuthTokenType.REFRESH);
    when(createNewAccessTokenUseCase.execute(user)).thenReturn(accessToken);
    when(createRefreshTokenUseCase.execute(eq(user), isNull())).thenReturn(refreshToken);

    var response = loginUseCase.execute(buildRequest(false));

    assertEquals("access-hash", response.authToken());
    assertEquals("refresh-hash", response.refreshToken());
    assertInstanceOf(LoginResponse.class, response);
  }

  @Test
  @DisplayName("Verify the flow is executed in order: revoke sessions, then create tokens and session")
  void verify_execution_order() {
    var accessToken = buildToken("access-hash", AuthTokenType.ACCESS);
    var refreshToken = buildToken("refresh-hash", AuthTokenType.REFRESH);
    when(createNewAccessTokenUseCase.execute(user)).thenReturn(accessToken);
    when(createRefreshTokenUseCase.execute(eq(user), isNull())).thenReturn(refreshToken);

    loginUseCase.execute(buildRequest(false));

    InOrder inOrder = inOrder(sessionRepo, createNewAccessTokenUseCase, createRefreshTokenUseCase, createAndSaveNewSessionUseCase);
    inOrder.verify(sessionRepo).revokeAllByUser(user.getId());
    inOrder.verify(createNewAccessTokenUseCase).execute(user);
    inOrder.verify(createRefreshTokenUseCase).execute(user, null);
    inOrder.verify(createAndSaveNewSessionUseCase).execute(user.getId(), null, refreshToken);
  }
}
