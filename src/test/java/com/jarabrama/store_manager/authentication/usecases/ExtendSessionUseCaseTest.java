package com.jarabrama.store_manager.authentication.usecases;

import com.jarabrama.store_manager.authentication.domain.AuthTokenRepositoryPort;
import com.jarabrama.store_manager.authentication.domain.SessionRepositoryPort;
import com.jarabrama.store_manager.authentication.domain.exceptions.SessionException;
import com.jarabrama.store_manager.authentication.domain.model.AuthToken;
import com.jarabrama.store_manager.authentication.domain.model.AuthTokenType;
import com.jarabrama.store_manager.authentication.domain.model.Session;
import com.jarabrama.store_manager.authentication.domain.model.SystemRole;
import com.jarabrama.store_manager.authentication.domain.model.SystemUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExtendSessionUseCaseTest {

  private static final String NO_SESSION_MESSAGE =
          "No se encontró ninguna sesión activa. Por favor, inicia sesión nuevamente";
  private static final String EXPIRED_SESSION_MESSAGE =
          "Tu sesión ha expirado. Por favor, inicia sesión nuevamente";

  @Mock
  private SessionRepositoryPort sessionRepo;

  @Mock
  private AuthTokenRepositoryPort authTokenRepo;

  @InjectMocks
  private ExtendSessionUseCase extendSessionUseCase;

  private SystemUser user;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    this.user = SystemUser.builder()
            .id(UUID.randomUUID())
            .username("admin")
            .role(SystemRole.EMPLOYEE)
            .build();
  }

  private AuthToken buildToken(Instant expiresAt, boolean revoked) {
    return AuthToken.builder()
            .id(UUID.randomUUID())
            .userId(user.getId())
            .tokenType(AuthTokenType.REFRESH)
            .expiresAt(expiresAt)
            .revoked(revoked)
            .build();
  }

  private Session buildSession(UUID trustedDeviceId, Instant expiresAt, boolean revoked, AuthToken refreshToken) {
    return Session.builder()
            .id(UUID.randomUUID())
            .userId(user.getId())
            .trustedDeviceId(trustedDeviceId)
            .lastActivityAt(Instant.now().minus(Duration.ofMinutes(1)))
            .expiresAt(expiresAt)
            .revoked(revoked)
            .refreshToken(refreshToken)
            .build();
  }

  private void assertApproximately(Instant actual, Instant expected) {
    assertNotNull(actual);
    assertTrue(actual.isAfter(expected.minusSeconds(2)));
    assertTrue(actual.isBefore(expected.plusSeconds(2)));
  }

  @Test
  @DisplayName("When user has no session should throw session exception and do nothing")
  void when_user_has_no_session_should_throw_session_exception() {
    when(sessionRepo.findLastByUser(user)).thenReturn(Optional.empty());

    var ex = assertThrows(SessionException.class, () -> extendSessionUseCase.execute(user));
    assertEquals(NO_SESSION_MESSAGE, ex.getMessage());

    verify(authTokenRepo, never()).updateExpirationTimeout(any(), any());
    verify(sessionRepo, never()).updateExpirationTimeout(any(), any());
    verify(sessionRepo, never()).updateLastActivityAt(any(), any());
  }

  @Test
  @DisplayName("When session is expired should throw session exception")
  void when_session_is_expired_should_throw_session_exception() {
    var token = buildToken(Instant.now().minus(Duration.ofMinutes(1)), false);
    var session = buildSession(UUID.randomUUID(), Instant.now().minus(Duration.ofMinutes(1)), false, token);
    when(sessionRepo.findLastByUser(user)).thenReturn(Optional.of(session));

    var ex = assertThrows(SessionException.class, () -> extendSessionUseCase.execute(user));
    assertEquals(EXPIRED_SESSION_MESSAGE, ex.getMessage());

    verify(authTokenRepo, never()).updateExpirationTimeout(any(), any());
    verify(sessionRepo, never()).updateExpirationTimeout(any(), any());
  }

  @Test
  @DisplayName("When session is revoked should throw session exception")
  void when_session_is_revoked_should_throw_session_exception() {
    var token = buildToken(Instant.now().plus(Duration.ofDays(1)), false);
    var session = buildSession(UUID.randomUUID(), Instant.now().plus(Duration.ofDays(1)), true, token);
    when(sessionRepo.findLastByUser(user)).thenReturn(Optional.of(session));

    var ex = assertThrows(SessionException.class, () -> extendSessionUseCase.execute(user));
    assertEquals(EXPIRED_SESSION_MESSAGE, ex.getMessage());

    verify(authTokenRepo, never()).updateExpirationTimeout(any(), any());
    verify(sessionRepo, never()).updateExpirationTimeout(any(), any());
  }

  @Test
  @DisplayName("When refresh token is revoked should throw session exception")
  void when_refresh_token_is_revoked_should_throw_session_exception() {
    var token = buildToken(Instant.now().plus(Duration.ofDays(1)), true);
    var session = buildSession(UUID.randomUUID(), Instant.now().plus(Duration.ofDays(1)), false, token);
    when(sessionRepo.findLastByUser(user)).thenReturn(Optional.of(session));

    var ex = assertThrows(SessionException.class, () -> extendSessionUseCase.execute(user));
    assertEquals(EXPIRED_SESSION_MESSAGE, ex.getMessage());

    verify(authTokenRepo, never()).updateExpirationTimeout(any(), any());
    verify(sessionRepo, never()).updateExpirationTimeout(any(), any());
  }

  @Test
  @DisplayName("When refresh token is null should throw session exception")
  void when_refresh_token_is_null_should_throw_session_exception() {
    var session = buildSession(UUID.randomUUID(), Instant.now().plus(Duration.ofDays(1)), false, null);
    when(sessionRepo.findLastByUser(user)).thenReturn(Optional.of(session));

    var ex = assertThrows(SessionException.class, () -> extendSessionUseCase.execute(user));
    assertEquals(EXPIRED_SESSION_MESSAGE, ex.getMessage());

    verify(authTokenRepo, never()).updateExpirationTimeout(any(), any());
    verify(sessionRepo, never()).updateExpirationTimeout(any(), any());
  }

  @Test
  @DisplayName("When session is valid on an untrusted device should extend expiration by 15 minutes from now")
  void when_valid_session_on_untrusted_device_should_extend_by_15_minutes() {
    var token = buildToken(Instant.now().plus(Duration.ofMinutes(5)), false);
    var session = buildSession(null, Instant.now().plus(Duration.ofMinutes(5)), false, token);
    when(sessionRepo.findLastByUser(user)).thenReturn(Optional.of(session));

    extendSessionUseCase.execute(user);

    var expected = Instant.now().plus(Duration.ofMinutes(15));

    var tokenCaptor = ArgumentCaptor.forClass(Instant.class);
    verify(authTokenRepo).updateExpirationTimeout(eq(token.getId()), tokenCaptor.capture());
    assertApproximately(tokenCaptor.getValue(), expected);

    var sessionCaptor = ArgumentCaptor.forClass(Instant.class);
    verify(sessionRepo).updateExpirationTimeout(eq(session.getId()), sessionCaptor.capture());
    assertApproximately(sessionCaptor.getValue(), expected);

    var activityCaptor = ArgumentCaptor.forClass(Instant.class);
    verify(sessionRepo).updateLastActivityAt(eq(session.getId()), activityCaptor.capture());
    assertApproximately(activityCaptor.getValue(), Instant.now());
  }

  @Test
  @DisplayName("When session is valid on a trusted device should extend expiration by 5 days from now")
  void when_valid_session_on_trusted_device_should_extend_by_5_days() {
    var token = buildToken(Instant.now().plus(Duration.ofDays(1)), false);
    var session = buildSession(UUID.randomUUID(), Instant.now().plus(Duration.ofDays(1)), false, token);
    when(sessionRepo.findLastByUser(user)).thenReturn(Optional.of(session));

    extendSessionUseCase.execute(user);

    var expected = Instant.now().plus(Duration.ofDays(5));

    var tokenCaptor = ArgumentCaptor.forClass(Instant.class);
    verify(authTokenRepo).updateExpirationTimeout(eq(token.getId()), tokenCaptor.capture());
    assertApproximately(tokenCaptor.getValue(), expected);

    var sessionCaptor = ArgumentCaptor.forClass(Instant.class);
    verify(sessionRepo).updateExpirationTimeout(eq(session.getId()), sessionCaptor.capture());
    assertApproximately(sessionCaptor.getValue(), expected);

    verify(sessionRepo).updateLastActivityAt(eq(session.getId()), any(Instant.class));
  }
}
