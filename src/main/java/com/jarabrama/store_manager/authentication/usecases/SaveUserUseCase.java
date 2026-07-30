package com.jarabrama.store_manager.authentication.usecases;

import com.jarabrama.store_manager.authentication.domain.SystemUserRepositoryPort;
import com.jarabrama.store_manager.authentication.domain.exceptions.InvalidNewUserException;
import com.jarabrama.store_manager.authentication.domain.model.SystemRole;
import com.jarabrama.store_manager.authentication.domain.model.SystemUser;
import com.jarabrama.store_manager.authentication.presentation.dto.NewUserRequest;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class SaveUserUseCase {

  private final SystemUserRepositoryPort userRepository;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;

  public String execute(NewUserRequest newUser) {
    if (newUser.username().length() < 3) {
      throw new InvalidNewUserException("El nombre de usuario debe tener al menos tres caracteres");
    } else if (newUser.username().length() > 20) {
      throw new InvalidNewUserException("El nombre de usuario no debe superar los 20 caracteres");
    } else if (newUser.password().length() < 6) {
      throw new InvalidNewUserException("La contraseña debe contener al menos 6 caracteres");
    } else if (newUser.password().length() > 20) {
      throw new InvalidNewUserException("La contraseña no puede superar los 20 caracteres");
    }
    var passHash = bCryptPasswordEncoder.encode(newUser.password());
    var user = SystemUser.builder().role(SystemRole.EMPLOYEE)
            .username(newUser.username())
            .passwordHash(passHash)
            .build();

    userRepository.save(user);
    return "Usuario creado exitosamente";
  }
}
