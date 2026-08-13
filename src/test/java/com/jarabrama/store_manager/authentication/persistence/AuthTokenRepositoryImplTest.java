package com.jarabrama.store_manager.authentication.persistence;

import com.jarabrama.store_manager.authentication.domain.SystemUserRepositoryPort;
import com.jarabrama.store_manager.authentication.domain.exceptions.UserNotFoundException;
import com.jarabrama.store_manager.authentication.domain.model.AuthToken;
import com.jarabrama.store_manager.authentication.domain.model.AuthTokenType;
import com.jarabrama.store_manager.authentication.domain.model.SystemRole;
import com.jarabrama.store_manager.authentication.domain.model.SystemUser;
import com.jarabrama.store_manager.authentication.persistence.entities.AuthTokenEntity;
import com.jarabrama.store_manager.authentication.persistence.entities.SystemUserEntity;
import com.jarabrama.store_manager.authentication.persistence.jpa.AuthTokenJpaRepository;
import com.jarabrama.store_manager.authentication.persistence.jpa.SystemUserJpaRepository;
import com.jarabrama.store_manager.authentication.persistence.mappers.AuthTokenEntityMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthTokenRepositoryImplTest {

  @Mock
  private AuthTokenJpaRepository jpaRepo;

  @Mock
  private SystemUserJpaRepository userRepo;

  @Spy
  private AuthTokenEntityMapper mapper;

  @InjectMocks
  private AuthTokenRepositoryImpl sut;


  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  private AuthToken token;
  private AuthTokenEntity tokenEntity;
  private SystemUserEntity user;

  private void buildToken() {
    this.token = AuthToken.builder()
            .tokenType(AuthTokenType.ACCESS)
            .tokenHash("hash")
            .id(UUID.randomUUID())
            .revoked(false)
            .createdAt(Instant.now())
            .expiresAt(Instant.now().plus(Duration.ofMinutes(30)))
            .build();
  }

  private void buildTokenEntity() {
    this.tokenEntity = AuthTokenEntity.builder()
            .tokenType(AuthTokenType.ACCESS)
            .tokenHash("hash")
            .id(UUID.randomUUID())
            .revoked(false)
            .createdAt(Instant.now())
            .expiresAt(Instant.now().plus(Duration.ofMinutes(30)))
            .build();
  }

  private void buildUser() {
    this.user = SystemUserEntity.builder()
            .username("admin")
            .passwordHash("admin")
            .id(UUID.randomUUID())
            .role(SystemRole.EMPLOYEE)
            .build();
  }

  @Test
  @DisplayName("When user not found then throw UserNotFoundException")
  void when_user_not_found_then_throw_exception() {
    buildToken();
    buildUser();
    var token = this.token;
    token.setUserId(user.getId());

    when(userRepo.findById(user.getId())).thenReturn(Optional.empty());

    var ex = assertThrows(UserNotFoundException.class, () -> sut.save(token));
    assertEquals("Usuario no encontrado", ex.getMessage());
  }

  @Test
  @DisplayName("When user founded then map to entity and save it")
  void when_user_founded_then_saves_it() {
    buildToken();
    buildTokenEntity();
    buildUser();
    var token = this.token;
    token.setUserId(user.getId());

    when(userRepo.findById(user.getId())).thenReturn(Optional.of(user));
    when(jpaRepo.save(any(AuthTokenEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

    var saved = sut.save(token);

    var captor = ArgumentCaptor.forClass(AuthTokenEntity.class);
    verify(jpaRepo).save(captor.capture());
    assertEquals(user, captor.getValue().getUser());
    assertEquals(token.getId(), saved.getId());
    assertEquals(token.getTokenHash(), saved.getTokenHash());
    assertEquals(token.getTokenType(), saved.getTokenType());
    assertEquals(token.getCreatedAt(), saved.getCreatedAt());
    assertEquals(token.getExpiresAt(), saved.getExpiresAt());
    assertEquals(token.isRevoked(), saved.isRevoked());
  }

}