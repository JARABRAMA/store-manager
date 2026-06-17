package com.jarabrama.store_manager.inventory.infraestructure.persistence.entities.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.jarabrama.store_manager.inventory.domain.model.Product;
import com.jarabrama.store_manager.inventory.infraestructure.persistence.entities.CategoryEntity;
import com.jarabrama.store_manager.inventory.infraestructure.persistence.entities.ProductEntity;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

public class ProductEntityMapperTest {

  private final ProductEntityMapper entityMapper = new ProductEntityMapper();

  @Test
  void mapProductToEntity() {
    var expirationDate = LocalDate.now().plusDays(15);

    var expected = ProductEntity.builder()
      .name("My Product")
      .price(2_400)
      .stock(12)
      .expiresAt(expirationDate)
      .imageUrl("http://some.url")
      .build();

    var domainClass = Product.builder()
      .name("My Product")
      .price(2_400)
      .stock(12)
      .expiresAt(expirationDate)
      .imageUrl("http://some.url")
      .build();

    assertEquals(expected, entityMapper.toEntity(domainClass));
  }

  @Test
  void mapEntityToDomainWithAllProperties() {
    var id = UUID.randomUUID();
    var expirationDate = LocalDate.now().plusDays(4);
    var categories = List.<CategoryEntity>of(
      new CategoryEntity(1l, "Cat 1"),
      new CategoryEntity(2l, "Cat 2"),
      new CategoryEntity(3l, "Cat 3")
    );

    var entity = ProductEntity.builder()
      .name("product 1")
      .id(id)
      .expiresAt(expirationDate)
      .description("Some description")
      .price(2_000)
      .stock(3)
      .imageUrl("http://some_image")
      .categories(categories)
      .build();

    var actualDomainProduct = entityMapper.toDomain(entity);

    var expectedCategories = categories
      .stream()
      .map(c -> c.getName())
      .toList();

    var expectedDomainProduct = Product.builder()
      .name("product 1")
      .id(id)
      .expiresAt(expirationDate)
      .description("Some description")
      .price(2_000)
      .stock(3)
      .imageUrl("http://some_image")
      .categories(expectedCategories)
      .build();

    assertEquals(expectedDomainProduct, actualDomainProduct);
  }

  @Test
  void mapEntityToDomainWithNoOptionalValues() {
    var id = UUID.randomUUID();
    var entity = ProductEntity.builder()
      .name("product 1")
      .id(id)
      .price(2_000)
      .stock(3)
      .build();

    var expectedDomainProduct = Product.builder()
      .name("product 1")
      .id(id)
      .expiresAt(null)
      .description(null)
      .price(2_000)
      .stock(3)
      .imageUrl(null)
      .categories(null)
      .build();

    var actualDomainProduct = entityMapper.toDomain(entity);

    assertEquals(expectedDomainProduct, actualDomainProduct);
  }
}
