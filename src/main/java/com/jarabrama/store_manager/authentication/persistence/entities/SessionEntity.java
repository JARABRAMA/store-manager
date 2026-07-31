package com.jarabrama.store_manager.authentication.persistence.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "sessions", schema = "authentication")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private SystemUserEntity user;

  @JoinColumn(name = "auth_token_id", nullable = false, referencedColumnName = "id")
  @OneToOne(fetch = FetchType.LAZY)
  private AuthTokenEntity refreshTokenHash;

  @JoinColumn(name="trusted_device_id")
  @ManyToOne(fetch = FetchType.LAZY)
  private TrustedDeviceEntity trustedDevice;

  @Column(name = "device_fingerprint")
  private String deviceFingerprint;

  @Column(name = "device_label")
  private String deviceLabel;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @Column(name = "last_activity_at", nullable = false)
  private Instant lastActivityAt;

  @Column(name = "expires_at", nullable = false)
  private Instant expiresAt;

  @Column(name = "revoked", nullable = false)
  private boolean revoked;

  @PrePersist
  void onCreate() {
    this.createdAt = Instant.now();
    this.lastActivityAt = Instant.now();
  }
}
