package com.jarabrama.store_manager.inventory.application.model.mappers;

import com.jarabrama.store_manager.inventory.application.model.dtos.UpdateProductRequest;
import com.jarabrama.store_manager.inventory.domain.model.Product;
import org.springframework.stereotype.Component;

@Component
public class UpdateProductRequestMapper {

  public Product toDomain(UpdateProductRequest req) {
    return Product.builder()
      .name(req.name())
      .description(req.description())
      .price(req.price())
      .stock(req.stock())
      .imageUrl(req.imageUrl())
      .categories(req.categories())
      .build();
  }
}
