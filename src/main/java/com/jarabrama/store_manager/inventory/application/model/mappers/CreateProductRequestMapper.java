package com.jarabrama.store_manager.inventory.application.model.mappers;

import com.jarabrama.store_manager.inventory.application.model.dtos.CreateProductRequest;
import com.jarabrama.store_manager.inventory.domain.model.Product;
import org.springframework.stereotype.Component;

@Component
public class CreateProductRequestMapper {

  public Product toDomain(CreateProductRequest req) {
    return Product.builder()
      .name(req.name())
      .description(req.description())
      .price(req.price())
      .stock(req.stock())
      .expiresAt(req.expiresAt())
      .imageUrl(req.imageUrl())
      .categories(req.categories())
      .build();
  }
}
