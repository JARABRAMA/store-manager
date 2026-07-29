package com.jarabrama.store_manager.authentication.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.util.UUID;

import org.springframework.data.annotation.Id;

import com.jarabrama.store_manager.authentication.domain.model.SystemRole;

import lombok.Builder;
import lombok.Data;


/**
 * SystemUserEntity
 */
@Entity
@Table(name = "system_users", schema = "authentication")
@Builder
@Data
public class SystemUserEntity {

  @Id
  @Column(nullable = false)
  private UUID id;

  @Column(nullable = false, unique = true)
  private String username;

  @Column(name = "password_hash", nullable = false)
  private String passwordHash;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private SystemRole  role;
}
