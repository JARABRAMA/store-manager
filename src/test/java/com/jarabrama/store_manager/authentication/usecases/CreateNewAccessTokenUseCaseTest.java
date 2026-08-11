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
import org.junit.jupiter.api.condition.DisabledIfSystemProperties;
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

class CreateNewAccessTokenUseCaseTest {
  @Mock
  private JwtService jwtService;

  @Mock
  private AuthTokenRepositoryPort authTokenRepositoryPort;

  @InjectMocks
  private CreateNewAccessTokenUseCase createNewAccessTokenUseCase;

  @BeforeEach
  void setUp() {
    // initialize an user
    this.user = SystemUser.builder()
            .username("admin")
            .passwordHash("admin")
            .role(SystemRole.EMPLOYEE)
            .id(UUID.randomUUID())
            .build();

    MockitoAnnotations.openMocks(this);
  }

  private SystemUser user;

  @Test
  void test_createNewAccessToken_generateTokenHash() {
    createNewAccessTokenUseCase.execute(user);

    verify(jwtService).generateToken(argThat(request -> request.username().equals(user.getUsername()) &&
            request.userRole().equals(SystemRole.EMPLOYEE) &&
            request.tokenType().equals(AuthTokenType.ACCESS) &&
            request.trustedDeviceId() == null));
  }

  @Test
  @DisplayName("When create new access token hash verify that expiration time is of 5 minutes")
  void test_createNewAccessToken_generateTokenHash_has_expiration_time_of_5_minutes() {
    var before = Instant.now().plus(Duration.ofMinutes(5));
    createNewAccessTokenUseCase.execute(user);
    var after = Instant.now().plus(Duration.ofMinutes(5));

    verify(jwtService).generateToken(argThat(request ->
            TestUtils.isBetweenToDates(request.expirationTime(), before, after)));

  }

  @Test
  @DisplayName("Verify that auth token saves with user and token hash and token type correct data")
  void verify_that_useCase_actually_saves_the_access_token() {
    when(jwtService.generateToken(any())).thenReturn("tokenHash");

    createNewAccessTokenUseCase.execute(user);

    verify(authTokenRepositoryPort).save(argThat(token ->
            token.getTokenType().equals(AuthTokenType.ACCESS) &&
                    !token.isRevoked() &&
                    token.getUserId().equals(user.getId()) &&
                    token.getTokenHash().equals("tokenHash")
    ));
  }

  @Test
  @DisplayName("Verify that auth token saves with creation date and expiration date correct values")
  void test_that_useCase_saves_accessToken_with_correct_dates() {
    when(jwtService.generateToken(any())).thenReturn("tokenHash");

    var before = Instant.now();
    createNewAccessTokenUseCase.execute(user);
    var after = Instant.now();

    verify(authTokenRepositoryPort).save(argThat(token ->
            TestUtils.isBetweenToDates(token.getCreatedAt(), before, after) &&
                    TestUtils.isBetweenToDates(token.getExpiresAt(), before.plus(Duration.ofMinutes(5)), after.plus(Duration.ofMinutes(5)))
    ));
  }

  @Test
  @DisplayName("Verify that use case actually returns what was saved in repository")
  void test_that_useCase_returns_what_was_saved_in_repository() {
    var token = AuthToken.builder()
            .userId(user.getId())
            .tokenHash("tokenHash")
            .revoked(false)
            .tokenType(AuthTokenType.ACCESS)
            .build();
    when(jwtService.generateToken(any())).thenReturn("tokenHash");
    when(authTokenRepositoryPort.save(any())).thenReturn(token);
    var actual = createNewAccessTokenUseCase.execute(user);
    assertEquals(token, actual);
  }


}