package com.jarabrama.store_manager.authentication.persistence.entities;

import com.jarabrama.store_manager.authentication.domain.model.AuthTokenType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "auth_token", schema = "authentication")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthTokenEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "token_hash", unique = true, nullable = false)
  private String tokenHash;

  @JoinColumn(name = "user_id",nullable = false, referencedColumnName = "id")
  @ManyToOne(fetch = FetchType.LAZY)
  private SystemUserEntity user;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Enumerated(EnumType.STRING)
  @Column(name = "token_type",  nullable = false)
  private AuthTokenType tokenType;

  @Column(name = "expiresAt", nullable = false)
  private Instant expiresAt;

  @Column
  private boolean revoked;

  @PrePersist
  void prePersists() {
    this.createdAt = Instant.now();
  }
}
