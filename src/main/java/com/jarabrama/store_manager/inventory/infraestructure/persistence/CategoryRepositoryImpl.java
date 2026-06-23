package com.jarabrama.store_manager.inventory.infraestructure.persistence;

import com.jarabrama.store_manager.inventory.infraestructure.persistence.entities.mappers.CategoryEntityMapper;
import com.jarabrama.store_manager.inventory.infraestructure.persistence.jpa.CategoryJpaRepo;
import com.jarabrama.store_manager.inventory.infraestructure.ports.out.CategoryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepository {

  private final CategoryJpaRepo categoryRepo;

  private final CategoryEntityMapper categoryMapper;

  @Override
  public boolean alreadyExists(String name) {
    return categoryRepo.existsByName(name);
  }

  @Override
  public void saveAll(List<String> categories) {
    var categoryEntities = categories
      .stream()
      .map(categoryMapper::toEntity)
      .toList();
    categoryRepo.saveAll(categoryEntities);
  }

  @Override
  public List<String> findAll() {
    return categoryRepo
      .findAll()
      .stream()
      .map(c -> c.getName())
      .toList();
  }
}
