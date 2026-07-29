package com.jarabrama.store_manager.authentication.domain.model;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SystemUser {
  private UUID id;
  private String username;
  private String passwordHash;
  private SystemRole role;
}
