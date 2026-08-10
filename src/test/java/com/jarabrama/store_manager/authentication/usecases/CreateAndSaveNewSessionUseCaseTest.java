package com.jarabrama.store_manager.authentication.usecases;

import com.jarabrama.store_manager.authentication.domain.SessionRepositoryPort;
import com.jarabrama.store_manager.authentication.domain.model.AuthToken;
import com.jarabrama.store_manager.authentication.domain.model.AuthTokenType;
import com.jarabrama.store_manager.authentication.domain.model.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreateAndSaveNewSessionUseCaseTest {
  @Mock
  private SessionRepositoryPort sessionRepositoryPort;

  @InjectMocks
  private CreateAndSaveNewSessionUseCase createAndSaveNewSessionUseCase;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  private AuthToken refreshToken;
  private final UUID userId = UUID.randomUUID();
  private final UUID trustedDeviceId = UUID.randomUUID();

  private void buildRefreshToken() {
    this.refreshToken = AuthToken.builder()
            .id(UUID.randomUUID())
            .tokenHash("tokenHash")
            .userId(userId)
            .tokenType(AuthTokenType.REFRESH)
            .createdAt(Instant.now())
            .expiresAt(Instant.now().plusSeconds(300))
            .revoked(false)
            .build();
  }

  @Test
  @DisplayName("Verify that session saves with user, trusted device and refresh token correct data")
  void verify_that_useCase_actually_saves_the_new_session() {
    buildRefreshToken();

    createAndSaveNewSessionUseCase.execute(userId, trustedDeviceId, refreshToken);

    verify(sessionRepositoryPort).save(argThat(session ->
            session.getUserId().equals(userId) &&
                    session.getTrustedDeviceId().equals(trustedDeviceId) &&
                    session.getRefreshToken().equals(refreshToken) &&
                    !session.isRevoked()
    ));
  }

  @Test
  @DisplayName("Verify that session saves with last activity and expiration dates from refresh token")
  void verify_that_session_dates_are_taken_from_refresh_token() {
    buildRefreshToken();

    createAndSaveNewSessionUseCase.execute(userId, trustedDeviceId, refreshToken);

    verify(sessionRepositoryPort).save(argThat(session ->
            session.getLastActivityAt().equals(refreshToken.getCreatedAt()) &&
                    session.getExpiresAt().equals(refreshToken.getExpiresAt())
    ));
  }

  @Test
  @DisplayName("Verify that session can be created without a trusted device")
  void verify_that_session_saves_without_trusted_device() {
    buildRefreshToken();

    createAndSaveNewSessionUseCase.execute(userId, null, refreshToken);

    verify(sessionRepositoryPort).save(argThat(session ->
            session.getUserId().equals(userId) &&
                    session.getTrustedDeviceId() == null &&
                    session.getRefreshToken().equals(refreshToken)
    ));
  }

  @Test
  @DisplayName("Verify that use case actually returns what was saved in repository")
  void test_that_useCase_returns_what_was_saved_in_repository() {
    buildRefreshToken();

    var session = Session.builder()
            .id(UUID.randomUUID())
            .userId(userId)
            .trustedDeviceId(trustedDeviceId)
            .lastActivityAt(refreshToken.getCreatedAt())
            .expiresAt(refreshToken.getExpiresAt())
            .revoked(false)
            .refreshToken(refreshToken)
            .build();
    when(sessionRepositoryPort.save(any())).thenReturn(session);
    var actual = createAndSaveNewSessionUseCase.execute(userId, trustedDeviceId, refreshToken);
    assertEquals(session, actual);
  }
}
