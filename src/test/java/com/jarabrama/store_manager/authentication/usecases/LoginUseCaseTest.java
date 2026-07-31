package com.jarabrama.store_manager.authentication.usecases;

import com.jarabrama.store_manager.authentication.domain.SystemUserRepositoryPort;
import com.jarabrama.store_manager.authentication.domain.exceptions.InvalidCredentialsException;
import com.jarabrama.store_manager.authentication.domain.model.SystemUser;
import com.jarabrama.store_manager.authentication.presentation.dto.LoginRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LoginUseCaseTest {

  @Mock
  private SystemUserRepositoryPort userRepo;

  @Mock
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  private LoginUseCase loginUseCase;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void check_it_actually_search_user_by_username() {
    var request = new LoginRequest("admin", "admin");
    try {
      loginUseCase.execute(request);
      verify(userRepo).findByUsername("admin");
    } catch (RuntimeException e) {
      assertInstanceOf(InvalidCredentialsException.class, e);
    }
  }

  @Test
  void if_user_not_found_by_username_should_throw_invalid_credentials() {
    var request = new LoginRequest("admin", "admin");
    when(userRepo.findByUsername(request.username())).thenReturn(Optional.empty());

    var ex = assertThrows(InvalidCredentialsException.class, () -> loginUseCase.execute(request));
    assertEquals("Credenciales incorrectas", ex.getMessage());
  }

  @Test
  void if_user_password_does_not_match_password_should_throw_invalid_credentials() {
    var request = new LoginRequest("admin", "admin");
    var user = SystemUser.builder()
            .passwordHash("admin")
            .username("admin").build();
    when(userRepo.findByUsername(request.username())).thenReturn(Optional.of(user));
    when(passwordEncoder.matches(request.password(), user.getPasswordHash())).thenReturn(false);

    var ex = assertThrows(InvalidCredentialsException.class, () -> loginUseCase.execute(request));
    assertEquals("Credenciales incorrectas", ex.getMessage());
  }

}