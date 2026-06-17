package com.jarabrama.store_manager.inventory.application.ports.in;

import com.jarabrama.store_manager.inventory.application.model.dtos.CreateProductRequest;
import com.jarabrama.store_manager.inventory.application.model.dtos.PageResponse;
import com.jarabrama.store_manager.inventory.application.model.dtos.ProductResponse;
import com.jarabrama.store_manager.inventory.application.model.dtos.UpdateProductRequest;

public interface ProductUseCase {
  void saveProduct(CreateProductRequest req);

  PageResponse<ProductResponse> findProducts(
    String search,
    String category,
    Integer page
  );

  void updateProduct(String id, UpdateProductRequest req);
}
