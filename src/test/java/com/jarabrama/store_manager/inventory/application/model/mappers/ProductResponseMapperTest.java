package com.jarabrama.store_manager.inventory.application.model.mappers;

import com.jarabrama.store_manager.inventory.application.model.dtos.ProductResponse;
import com.jarabrama.store_manager.inventory.domain.model.Product;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class ProductResponseMapperTest {
  private ProductResponseMapper productResponseMapper;

  @BeforeEach
  void init() {
    productResponseMapper = new ProductResponseMapper();
  }

  @Test
  void mapFromDomainWithNotNullValues() {
    var domainClass = Product.builder()
            .id(UUID.randomUUID())
            .name("Producto 1")
            .description("Una descripcion del producto")
            .price(2_1000)
            .stock(3)
            .expiresAt(LocalDate.now().plusDays(23))
            .imageUrl("http://url-image")
            .categories(List.of())
            .build();

    var expected = ProductResponse.builder()
            .id(domainClass.getId().toString())
            .name("Producto 1")
            .description("Una descripcion del producto")
            .price(2_1000)
            .stock(3)
            .expireAt(LocalDate.now().plusDays(23))
            .imageUrl("http://url-image")
            .categories(List.of())
            .build();

    var actual = productResponseMapper.fromDomain(domainClass);

    Assertions.assertEquals(expected, actual);
  }

  @Test
  void mapFromDomainWithAllowedNullValues() {
    var domainClass = Product.builder()
            .id(UUID.randomUUID())
            .name("Producto 1")
            .description(null)
            .price(2_1000)
            .stock(3)
            .expiresAt(null)
            .imageUrl(null)
            .categories(List.of())
            .build();

    var expected = ProductResponse.builder()
            .id(domainClass.getId().toString())
            .name("Producto 1")
            .description(null)
            .price(2_1000)
            .stock(3)
            .expireAt(null)
            .imageUrl(null)
            .categories(List.of())
            .build();

    var actual = productResponseMapper.fromDomain(domainClass);

    Assertions.assertEquals(expected, actual);
  }
}
