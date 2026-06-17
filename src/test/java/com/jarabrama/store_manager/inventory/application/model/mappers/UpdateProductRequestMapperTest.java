package com.jarabrama.store_manager.inventory.application.model.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.jarabrama.store_manager.inventory.application.model.dtos.UpdateProductRequest;
import com.jarabrama.store_manager.inventory.domain.model.Product;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UpdateProductRequestMapperTest {

  private UpdateProductRequestMapper mapper;

  @BeforeEach
  void init() {
    mapper = new UpdateProductRequestMapper();
  }

  @Test
  void mapProductWithAllAttributes() {
    var categories = List.<String>of("category 1", "category 2", "category 3");
    var req = UpdateProductRequest.builder()
      .name("Product 1")
      .description("description of product 1")
      .stock(1)
      .price(Double.valueOf(3_900))
      .imageUrl("http://imageurl")
      .categories(categories)
      .build();

    var expected = Product.builder()
      .name("Product 1")
      .description("description of product 1")
      .stock(1)
      .price(3_900)
      .imageUrl("http://imageurl")
      .categories(categories)
      .build();

    var actual = mapper.toDomain(req);
    assertEquals(expected, actual);
  }

  @Test
  void mapProductWithNullAttributes() {
    var categories = List.<String>of("category 1", "category 2", "category 3");
    var req = UpdateProductRequest.builder()
      .name("Product 1")
      .stock(1)
      .price(Double.valueOf(3_900))
      .categories(categories)
      .build();

    var expected = Product.builder()
      .name("Product 1")
      .description(null)
      .stock(1)
      .price(3_900)
      .imageUrl(null)
      .categories(categories)
      .build();

    var actual = mapper.toDomain(req);
    assertEquals(expected, actual);
  }
}
