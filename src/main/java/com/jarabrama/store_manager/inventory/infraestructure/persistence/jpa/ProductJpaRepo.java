package com.jarabrama.store_manager.inventory.infraestructure.persistence.jpa;

import com.jarabrama.store_manager.inventory.infraestructure.persistence.entities.ProductEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProductJpaRepo
  extends
    JpaRepository<ProductEntity, UUID>,
    JpaSpecificationExecutor<ProductEntity>
{
  boolean existsByName(String name);
}
