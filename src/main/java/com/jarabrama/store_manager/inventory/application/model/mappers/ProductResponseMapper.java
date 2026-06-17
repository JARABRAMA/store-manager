package com.jarabrama.store_manager.inventory.application.model.mappers;

import com.jarabrama.store_manager.inventory.application.model.dtos.ProductResponse;
import com.jarabrama.store_manager.inventory.domain.model.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductResponseMapper {

  public ProductResponse fromDomain(Product product) {
    return ProductResponse.builder()
      .id(product.getId().toString())
      .name(product.getName())
      .description(product.getDescription())
      .price(product.getPrice())
      .stock(product.getStock())
      .expireAt(product.getExpiresAt())
      .imageUrl(product.getImageUrl())
      .categories(product.getCategories())
      .build();
  }
}
