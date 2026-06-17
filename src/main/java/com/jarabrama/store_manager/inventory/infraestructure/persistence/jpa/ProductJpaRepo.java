package com.jarabrama.store_manager.inventory.infraestructure.persistence.jpa;

import com.jarabrama.store_manager.inventory.infraestructure.persistence.entities.ProductEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductJpaRepo
  extends
    JpaRepository<ProductEntity, UUID>,
    JpaSpecificationExecutor<ProductEntity>
{
  boolean existsByName(String name);

  @Query(
    """
    SELECT COUNT(p) > 0
    FROM ProductEntity p
    WHERE name = :name AND id <> :id
    """
  )
  boolean existsByNameWithDifferentId(
    @Param("name") String name,
    @Param("id") UUID id
  );
}
