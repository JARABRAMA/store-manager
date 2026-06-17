package com.jarabrama.store_manager.inventory.infraestructure.persistence.entities.mappers;

import com.jarabrama.store_manager.inventory.domain.model.Product;
import com.jarabrama.store_manager.inventory.infraestructure.persistence.entities.CategoryEntity;
import com.jarabrama.store_manager.inventory.infraestructure.persistence.entities.ProductEntity;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ProductEntityMapper {

  public ProductEntity toEntity(Product product) {
    return ProductEntity.builder()
      .id(product.getId())
      .name(product.getName())
      .description(product.getDescription())
      .price(product.getPrice())
      .stock(product.getStock())
      .expiresAt(product.getExpiresAt())
      .imageUrl(product.getImageUrl())
      .build();
  }

  public Product toDomain(ProductEntity entity) {
    return Product.builder()
      .id(entity.getId())
      .name(entity.getName())
      .description(entity.getDescription())
      .price(entity.getPrice())
      .stock(entity.getStock())
      .expiresAt(entity.getExpiresAt())
      .imageUrl(entity.getImageUrl())
      .categories(getCategoryNames(entity.getCategories()))
      .build();
  }

  private List<String> getCategoryNames(List<CategoryEntity> categories) {
    if (categories == null) return null;
    return categories
      .stream()
      .map(c -> c.getName())
      .toList();
  }
}
