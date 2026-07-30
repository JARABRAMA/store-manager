package com.jarabrama.store_manager.authentication.persistence;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.jarabrama.store_manager.authentication.domain.exceptions.DatabaseException;
import com.jarabrama.store_manager.authentication.domain.model.SystemRole;
import com.jarabrama.store_manager.authentication.domain.model.SystemUser;
import com.jarabrama.store_manager.authentication.persistence.entities.SystemUserEntity;
import com.jarabrama.store_manager.authentication.persistence.jpa.SystemUserJpaRepository;
import com.jarabrama.store_manager.authentication.persistence.mappers.SystemUserEntityMapper;
import kotlin._Assertions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.internal.matchers.Any;
import org.springframework.dao.DataIntegrityViolationException;

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
}
