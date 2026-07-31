package com.jarabrama.store_manager.authentication.persistence.entities;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "trusted_devices", schema = "authentication")
public class TrustedDeviceEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private SystemUserEntity user;

  @Column(name = "device_token_hash", nullable = false, unique = true)
  private String deviceTokenHash; // valor de la cookie httpOnly, hasheado

  @Column(name = "device_label")
  private String deviceLabel;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @Column(name = "last_used_at", nullable = false)
  private Instant lastUsedAt;

  @Column(name = "revoked", nullable = false)
  private boolean revoked;

  @PrePersist
  void onCreate() {
    this.createdAt = Instant.now();
    this.lastUsedAt = Instant.now();
  }
}
