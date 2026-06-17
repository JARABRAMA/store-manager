package com.jarabrama.store_manager.inventory.infraestructure.ports.out;

import com.jarabrama.store_manager.inventory.domain.model.DomainPage;
import com.jarabrama.store_manager.inventory.domain.model.Product;

public interface ProductRepository {
  void save(Product product);
  boolean alreadyExists(String name);
  DomainPage<Product> findAll(
    String search,
    String category,
    int pageNumber,
    int pageSize
  );
}
