package com.jarabrama.store_manager.inventory.infraestructure.ports.out;

import java.util.List;

public interface CategoryRepository {
  boolean alreadyExists(String name);
  void saveAll(List<String> categories);
}
