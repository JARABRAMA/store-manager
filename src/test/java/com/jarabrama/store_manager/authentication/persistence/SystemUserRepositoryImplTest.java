package com.jarabrama.store_manager.authentication.persistence;

import com.jarabrama.store_manager.authentication.domain.exceptions.DatabaseException;
import com.jarabrama.store_manager.authentication.domain.model.SystemRole;
import com.jarabrama.store_manager.authentication.domain.model.SystemUser;
import com.jarabrama.store_manager.authentication.persistence.entities.SystemUserEntity;
import com.jarabrama.store_manager.authentication.persistence.jpa.SystemUserJpaRepository;
import com.jarabrama.store_manager.authentication.persistence.mappers.SystemUserEntityMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

/**
 * SystemUserRepositoryImplTest
 */
public class SystemUserRepositoryImplTest {

  @Mock
  private SystemUserJpaRepository jpaRepository;

  @Spy
  private SystemUserEntityMapper mapper = new SystemUserEntityMapper();

  @InjectMocks
  private SystemUserRepositoryImpl repository;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);
  }

  private final SystemUser validUser = SystemUser.builder()
          .username("username")
          .passwordHash("hashofthepassword")
          .role(SystemRole.ADMINISTRATOR)
          .build();

  @Test
  void save_user_throws_any_database_error() {
    doThrow(new RuntimeException("any message"))
            .when(jpaRepository)
            .save(any(SystemUserEntity.class));

    Assertions.assertThrows(DatabaseException.class, () -> repository.save(validUser));
  }

  @Test
  void saved_successfully_user_by_jpa() {
    when(jpaRepository.save(any(SystemUserEntity.class))).thenReturn(SystemUserEntity.builder().build());
    repository.save(validUser); // should do nothing
  }

  @Test
  void find_by_username_did_not_found_anything() {
    var username = "admin";
    when(jpaRepository.findByUsername(username)).thenReturn(Optional.empty());
    Assertions.assertEquals(Optional.empty(), repository.findByUsername(username));
  }

  @Test
  void find_by_username_found_an_user() {
    var entity = SystemUserEntity.builder().username("admin").build();
    when(jpaRepository.findByUsername(entity.getUsername())).thenReturn(Optional.of(entity));
    var expected = SystemUser.builder().username(entity.getUsername()).build();
    var actual = repository.findByUsername(entity.getUsername());

    Assertions.assertTrue(actual.isPresent());
    Assertions.assertEquals(expected, actual.get());
  }

  @Test
  void should_trows_database_error_when_repository_fails() {
    when(jpaRepository.findByUsername(any())).thenThrow(new RuntimeException("Some database error"));

    var error = Assertions.assertThrows(DatabaseException.class, () -> repository.findByUsername("username"));
    Assertions.assertEquals("Error de base de datos", error.getMessage());
  }
}
