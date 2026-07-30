package com.jarabrama.store_manager.authentication.persistence.entities;

import jakarta.persistence.*;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

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
@NoArgsConstructor
@AllArgsConstructor
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
