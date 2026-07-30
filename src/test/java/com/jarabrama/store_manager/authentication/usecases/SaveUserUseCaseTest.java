package com.jarabrama.store_manager.authentication.usecases;

import com.jarabrama.store_manager.authentication.domain.SystemUserRepositoryPort;
import com.jarabrama.store_manager.authentication.domain.exceptions.DatabaseException;
import com.jarabrama.store_manager.authentication.domain.exceptions.InvalidNewUserException;
import com.jarabrama.store_manager.authentication.domain.exceptions.UserAlreadyExistsException;
import com.jarabrama.store_manager.authentication.domain.model.SystemRole;
import com.jarabrama.store_manager.authentication.presentation.dto.NewUserRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;


class SaveUserUseCaseTest {

  @Mock
  private SystemUserRepositoryPort userRepository;

  @Spy
  private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

  @InjectMocks
  private SaveUserUseCase saveUserUseCase;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }


  @Test
  void to_short_user_password() {
    var request = new NewUserRequest("admin", "aaa");

    var exception = Assertions.assertThrows(InvalidNewUserException.class, () -> saveUserUseCase.execute(request));
    Assertions.assertEquals("La contraseña debe contener al menos 6 caracteres", exception.getMessage());
  }

  @Test
  void to_short_user_username() {
    var request = new NewUserRequest("ad", "password");
    var exception = Assertions.assertThrows(InvalidNewUserException.class, () -> saveUserUseCase.execute(request));
    Assertions.assertEquals("El nombre de usuario debe tener al menos tres caracteres", exception.getMessage());
  }

  @Test
  void to_long_user_password() {
    var request = new NewUserRequest("admin", "this_password_is_to_long_for_the_system");
    var exception = Assertions.assertThrows(InvalidNewUserException.class, () -> saveUserUseCase.execute(request));
    Assertions.assertEquals("La contraseña no puede superar los 20 caracteres", exception.getMessage());
  }

  @Test
  void to_long_user_username() {
    var request = new NewUserRequest("this_username_is_too_long_for_the_system", "password");
    var exception = Assertions.assertThrows(InvalidNewUserException.class, () -> saveUserUseCase.execute(request));
    Assertions.assertEquals("El nombre de usuario no debe superar los 20 caracteres", exception.getMessage());
  }

  @Test
  void should_actually_saves_user_when_is_valid() {
    var request = new NewUserRequest("admin", "password");
    var message = saveUserUseCase.execute(request);
    Assertions.assertEquals("Usuario creado exitosamente", message);
    verify(userRepository).save(any());
  }


  @Test
  void should_actually_encrypt_user_password() {
    var request = new NewUserRequest("admin", "password");
    saveUserUseCase.execute(request);
    verify(bCryptPasswordEncoder).encode(request.password());
  }

  @Test
  void new_users_should_has_employee_role() {
    var request = new NewUserRequest("admin", "password");
    saveUserUseCase.execute(request);
    verify(userRepository).save(argThat(user -> user.getRole() == SystemRole.EMPLOYEE));
  }

  @Test
  void propagate_error_when_database_exception() {
    var request = new NewUserRequest("admin", "password");
    doThrow(new DatabaseException("Database error")).when(userRepository).save(any());
    Assertions.assertThrows(DatabaseException.class, () -> saveUserUseCase.execute(request));
  }

  @Test
  void propagate_error_when_user_already_exists() {
    var request = new NewUserRequest("admin", "password");
    doThrow(new UserAlreadyExistsException("User already exists")).when(userRepository).save(any());
    Assertions.assertThrows(UserAlreadyExistsException.class, () -> saveUserUseCase.execute(request));
  }

}