package com.jarabrama.store_manager.authentication.persistence.jpa;

import com.jarabrama.store_manager.authentication.persistence.entities.AuthTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuthTokenJpaRepository extends JpaRepository<AuthTokenEntity, UUID> {

}
