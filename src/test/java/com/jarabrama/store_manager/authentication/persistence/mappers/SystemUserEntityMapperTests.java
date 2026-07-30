package com.jarabrama.store_manager.authentication.persistence.mappers;

import com.jarabrama.store_manager.authentication.domain.model.SystemRole;
import com.jarabrama.store_manager.authentication.domain.model.SystemUser;
import com.jarabrama.store_manager.authentication.persistence.entities.SystemUserEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class SystemUserEntityMapperTests {


  private SystemUserEntityMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = new SystemUserEntityMapper();
  }

  @Test
  public void map_to_entity_whit_no_id() {
    SystemUser user = SystemUser.builder()
            .username("some username")
            .role(SystemRole.ADMINISTRATOR)
            .passwordHash("a password hash")
            .build();
    var expected = SystemUserEntity.builder()
            .username("some username")
            .role(SystemRole.ADMINISTRATOR)
            .passwordHash("a password hash")
            .build();

    var actual = mapper.fromDomain(user);

    Assertions.assertEquals(expected, actual);
  }

  @Test
  public void map_to_entity_with_id() {
    var id = UUID.randomUUID();
    SystemUser user = SystemUser.builder()
            .id(id)
            .username("some username")
            .role(SystemRole.ADMINISTRATOR)
            .passwordHash("a password hash")
            .build();
    var expected = SystemUserEntity.builder()
            .id(id)
            .username("some username")
            .role(SystemRole.ADMINISTRATOR)
            .passwordHash("a password hash")
            .build();
    var actual = mapper.fromDomain(user);
    Assertions.assertEquals(expected, actual);
  }
}
