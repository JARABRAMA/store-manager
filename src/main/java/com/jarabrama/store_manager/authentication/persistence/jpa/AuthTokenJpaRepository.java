package com.jarabrama.store_manager.authentication.persistence.jpa;

import com.jarabrama.store_manager.authentication.persistence.entities.AuthTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.UUID;

public interface AuthTokenJpaRepository extends JpaRepository<AuthTokenEntity, UUID> {

  @Modifying
  @Query("""
          UPDATE AuthTokenEntity t
              SET t.expiresAt = :expiresAt
              WHERE t.id = :id
          """)
  void updateExpiresAt(@Param("expiresAt") Instant expiresAt, @Param("id") UUID id);

  @Modifying
  @Query("""
              UPDATE AuthTokenEntity t
                  SET t.revoked = true
                  WHERE t.user.id = :id
          """)
  void revokeAllByUser(@Param("id") UUID id);
}
