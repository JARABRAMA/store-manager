package com.jarabrama.store_manager.authentication.usecases;

import com.jarabrama.store_manager.authentication.domain.AuthTokenRepositoryPort;
import com.jarabrama.store_manager.authentication.domain.SessionRepositoryPort;
import com.jarabrama.store_manager.authentication.domain.SystemUserRepositoryPort;
import com.jarabrama.store_manager.authentication.domain.exceptions.AuthException;
import com.jarabrama.store_manager.authentication.domain.model.SystemRole;
import com.jarabrama.store_manager.authentication.domain.model.SystemUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LogoutUseCaseTest {

  @Mock
  private SystemUserRepositoryPort userRepo;
  @Mock
  private SessionRepositoryPort sessionRepo;
  @Mock
  private AuthTokenRepositoryPort authTokenRepo;
  @InjectMocks
  private LogoutUseCase logoutUseCase;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  private SystemUser user;

  private void buildUser() {
    this.user  = SystemUser.builder()
            .id(UUID.randomUUID())
            .username("admin")
            .passwordHash("admin")
            .role(SystemRole.EMPLOYEE)
            .build();
  }

  @Test
  @DisplayName("When user id is not present in the system then throw an AuthException")
  void when_user_is_not_present_in_system_then_throw_exception() {
    buildUser();
    when(userRepo.findById(this.user.getId())).thenReturn(Optional.empty());

    var ex = assertThrows(AuthException.class, () -> logoutUseCase.execute(this.user.getId()));
    assertEquals("No se pudo cerrar sesión, Usuario invalido", ex.getMessage());
  }

  @Test
  @DisplayName("When user is valid then revoke all user sessions")
  void when_user_is_valid_then_revoke_all_user_sessions() {
    buildUser();
    when(userRepo.findById(this.user.getId())).thenReturn(Optional.of(this.user));

    logoutUseCase.execute(this.user.getId());
    verify(sessionRepo).revokeAllByUser(this.user.getId());
  }

  @Test
  @DisplayName("When user is valid then revoke all the user authentication tokens")
  void when_user_is_valid_then_revoke_all_user_tokens() {
    buildUser();
    when(userRepo.findById(this.user.getId())).thenReturn(Optional.of(this.user));

    verify(authTokenRepo).revokeAllByUser(user.getId())
  }
}