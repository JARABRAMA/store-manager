package com.jarabrama.store_manager.authentication.usecases;

import com.jarabrama.store_manager.TestUtils;
import com.jarabrama.store_manager.authentication.domain.AuthTokenRepositoryPort;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreateRefreshTokenUseCaseTest {
  @Mock
  private JwtService jwtService;

  @Mock
  private AuthTokenRepositoryPort authTokenRepositoryPort;

  @InjectMocks
  private CreateRefreshTokenUseCase createRefreshTokenUseCase;

  @BeforeEach
  void setUp() {
    this.user = SystemUser.builder()
            .username("admin")
            .passwordHash("admin")
            .role(SystemRole.EMPLOYEE)
            .id(UUID.randomUUID())
            .build();

    MockitoAnnotations.openMocks(this);
  }

  private SystemUser user;
  private final UUID trustedDeviceId = UUID.randomUUID();

  @Test
  @DisplayName("When create refresh token for trusted device generate token hash with correct data")
  void test_createRefreshToken_generateTokenHash_for_trusted_device() {
    createRefreshTokenUseCase.execute(user, trustedDeviceId);

    verify(jwtService).generateToken(argThat(request -> request.username().equals(user.getUsername()) &&
            request.userRole().equals(SystemRole.EMPLOYEE) &&
            request.tokenType().equals(AuthTokenType.REFRESH) &&
            request.trustedDeviceId().equals(trustedDeviceId)));
  }

  @Test
  @DisplayName("When create refresh token for not trusted device generate token hash with correct data")
  void test_createRefreshToken_generateTokenHash_for_not_trusted_device() {
    createRefreshTokenUseCase.execute(user, null);

    verify(jwtService).generateToken(argThat(request -> request.username().equals(user.getUsername()) &&
            request.userRole().equals(SystemRole.EMPLOYEE) &&
            request.tokenType().equals(AuthTokenType.REFRESH) &&
            request.trustedDeviceId() == null));
  }

  @Test
  @DisplayName("When create refresh token for trusted device verify that expiration time is of 5 days")
  void test_createRefreshToken_generateTokenHash_has_expiration_time_of_5_days() {
    var before = Instant.now().plus(Duration.ofDays(5));
    createRefreshTokenUseCase.execute(user, trustedDeviceId);
    var after = Instant.now().plus(Duration.ofDays(5));

    verify(jwtService).generateToken(argThat(request ->
            TestUtils.isBetweenToDates(request.expirationTime(), before, after)));
  }

  @Test
  @DisplayName("When create refresh token for not trusted device verify that expiration time is of 15 minutes")
  void test_createRefreshToken_generateTokenHash_has_expiration_time_of_15_minutes() {
    var before = Instant.now().plus(Duration.ofMinutes(15));
    createRefreshTokenUseCase.execute(user, null);
    var after = Instant.now().plus(Duration.ofMinutes(15));

    verify(jwtService).generateToken(argThat(request ->
            TestUtils.isBetweenToDates(request.expirationTime(), before, after)));
  }

  @Test
  @DisplayName("Verify that auth token saves with user and token hash and token type correct data")
  void verify_that_useCase_actually_saves_the_refresh_token() {
    when(jwtService.generateToken(any())).thenReturn("tokenHash");

    createRefreshTokenUseCase.execute(user, trustedDeviceId);

    verify(authTokenRepositoryPort).save(argThat(token ->
            token.getTokenType().equals(AuthTokenType.REFRESH) &&
                    !token.isRevoked() &&
                    token.getUserId().equals(user.getId()) &&
                    token.getTokenHash().equals("tokenHash")
    ));
  }

  @Test
  @DisplayName("Verify that auth token saves with creation date and expiration date correct values for trusted device")
  void test_that_useCase_saves_refreshToken_with_correct_dates_for_trusted_device() {
    when(jwtService.generateToken(any())).thenReturn("tokenHash");

    var before = Instant.now();
    createRefreshTokenUseCase.execute(user, trustedDeviceId);
    var after = Instant.now();

    verify(authTokenRepositoryPort).save(argThat(token ->
            TestUtils.isBetweenToDates(token.getCreatedAt(), before, after) &&
                    TestUtils.isBetweenToDates(token.getExpiresAt(), before.plus(Duration.ofDays(5)), after.plus(Duration.ofDays(5)))
    ));
  }

  @Test
  @DisplayName("Verify that auth token saves with creation date and expiration date correct values for not trusted device")
  void test_that_useCase_saves_refreshToken_with_correct_dates_for_not_trusted_device() {
    when(jwtService.generateToken(any())).thenReturn("tokenHash");

    var before = Instant.now();
    createRefreshTokenUseCase.execute(user, null);
    var after = Instant.now();

    verify(authTokenRepositoryPort).save(argThat(token ->
            TestUtils.isBetweenToDates(token.getCreatedAt(), before, after) &&
                    TestUtils.isBetweenToDates(token.getExpiresAt(), before.plus(Duration.ofMinutes(15)), after.plus(Duration.ofMinutes(15)))
    ));
  }

  @Test
  @DisplayName("Verify that use case actually returns what was saved in repository")
  void test_that_useCase_returns_what_was_saved_in_repository() {
    var token = AuthToken.builder()
            .userId(user.getId())
            .tokenHash("tokenHash")
            .revoked(false)
            .tokenType(AuthTokenType.REFRESH)
            .build();
    when(jwtService.generateToken(any())).thenReturn("tokenHash");
    when(authTokenRepositoryPort.save(any())).thenReturn(token);
    var actual = createRefreshTokenUseCase.execute(user, trustedDeviceId);
    assertEquals(token, actual);
  }
}
