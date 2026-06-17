package com.jarabrama.store_manager.inventory.infraestructure.persistence.entities.mappers;

import com.jarabrama.store_manager.inventory.infraestructure.persistence.entities.CategoryEntity;
import org.springframework.stereotype.Component;

@Component
public class CategoryEntityMapper {

  public CategoryEntity toEntity(String category) {
    return CategoryEntity.builder().name(category).build();
  }
}
