package com.jarabrama.store_manager.inventory.infraestructure.persistence.jpa;

import com.jarabrama.store_manager.inventory.infraestructure.persistence.entities.CategoryEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryJpaRepo extends JpaRepository<CategoryEntity, Long> {
  public List<CategoryEntity> findAllByNameIn(List<String> names);
  public boolean existsByName(String name);
}
