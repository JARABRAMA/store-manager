package com.jarabrama.store_manager.authentication.usecases;

import com.jarabrama.store_manager.authentication.domain.SessionRepositoryPort;
import com.jarabrama.store_manager.authentication.domain.SystemUserRepositoryPort;
import com.jarabrama.store_manager.authentication.domain.exceptions.InvalidCredentialsException;
import com.jarabrama.store_manager.authentication.domain.model.AuthTokenType;
import com.jarabrama.store_manager.authentication.domain.model.SystemRole;
import com.jarabrama.store_manager.authentication.domain.model.SystemUser;
import com.jarabrama.store_manager.authentication.presentation.dto.LoginRequest;
import com.jarabrama.store_manager.authentication.service.JwtService;
import com.jarabrama.store_manager.authentication.service.model.NewJwtTokenRequest;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class LoginUseCaseTest {

  @Mock
  private SystemUserRepositoryPort userRepo;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private SessionRepositoryPort sessionRepo;

  @Mock
  private JwtService jwtService;

  @InjectMocks
  private LoginUseCase loginUseCase;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void check_it_actually_search_user_by_username() {
    var request = LoginRequest.builder().username("admin").password("admin").build();
    try {
      loginUseCase.execute(request);
      verify(userRepo).findByUsername("admin");
    } catch (RuntimeException e) {
      assertInstanceOf(InvalidCredentialsException.class, e);
    }
  }

  @Test
  void if_user_not_found_by_username_should_throw_invalid_credentials() {
    var request = LoginRequest.builder().username("admin").password("admin").build();
    when(userRepo.findByUsername(request.username())).thenReturn(Optional.empty());

    var ex = assertThrows(InvalidCredentialsException.class, () -> loginUseCase.execute(request));
    assertEquals("Credenciales incorrectas", ex.getMessage());
  }

  @Test
  void if_user_password_does_not_match_password_should_throw_invalid_credentials() {
    var request = LoginRequest.builder().username("admin").password("admin").build();
    var user = SystemUser.builder()
            .passwordHash("admin")
            .username("admin").build();
    when(userRepo.findByUsername(request.username())).thenReturn(Optional.of(user));
    when(passwordEncoder.matches(request.password(), user.getPasswordHash())).thenReturn(false);

    var ex = assertThrows(InvalidCredentialsException.class, () -> loginUseCase.execute(request));
    assertEquals("Credenciales incorrectas", ex.getMessage());
  }

  @Test
  void verify_revoke_all_sessions_by_user() {
    // existing setup...
    when(jwtService.generateToken(any(NewJwtTokenRequest.class))).thenReturn("fake-token-hash");

    var mockClaims = mock(Claims.class);
    when(mockClaims.getIssuedAt()).thenReturn(new Date());
    when(mockClaims.getExpiration()).thenReturn(new Date(System.currentTimeMillis() + 100000));
    when(mockClaims.getSubject()).thenReturn("someUsername");
    when(mockClaims.get("userRole", String.class)).thenReturn(SystemRole.EMPLOYEE.toString());
    when(mockClaims.get("tokenType", String.class)).thenReturn(AuthTokenType.ACCESS.toString());
    // stub whatever other claims AuthToken.fromTokenClaims reads

    when(jwtService.getClaimsFromToken(anyString())).thenReturn(mockClaims);

    var userId = UUID.randomUUID();
    var request = LoginRequest.builder().username("admin").password("admin").build();
    var user = SystemUser.builder()
            .passwordHash("admin")
            .id(userId)
            .username("admin").build();

    when(userRepo.findByUsername(request.username())).thenReturn(Optional.of(user));
    when(passwordEncoder.matches(request.password(), user.getPasswordHash())).thenReturn(true);

    loginUseCase.execute(request);
    verify(sessionRepo).revokeAllByUser(userId);
  }


}