package com.jarabrama.store_manager.inventory.domain.model;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.jarabrama.store_manager.inventory.domain.exceptions.InvalidProductException;
import org.junit.jupiter.api.Test;

public class ProductTest {

  @Test
  void testInStockProduct() {
    var product = Product.builder().name("My product").stock(3).build();
    assertTrue(product.isInStock());
  }

  @Test
  void testNotInStockProduct() {
    var product = Product.builder().name("My product").stock(0).build();
    assertFalse(product.isInStock());
  }

  @Test
  void productWithBlankName() {
    var product = Product.builder().build();
    assertThrows(
      InvalidProductException.class,
      product::validate,
      "El nombre del producto es requerido"
    );
  }

  @Test
  void productWithToManyCharactersInName() {
    var product = Product.builder()
      .price(0)
      .stock(1_000)
      .name(
        "Product with a name with more than 50 characters between his attributes"
      )
      .build();

    assertThrows(
      InvalidProductException.class,
      product::validate,
      "El nombre de un producto no puede tener mas de 50 caracteres"
    );
  }

  @Test
  void productWithLessThanZeroInStock() {
    var product = Product.builder()
      .price(2_000)
      .stock(-4)
      .name("My Product")
      .build();

    assertThrows(
      InvalidProductException.class,
      product::validate,
      "Las unidades disponibles no pueden ser menores a cero"
    );
  }

  @Test
  void productWithDescriptionWithMoreThan100Characters() {
    var product = Product.builder()
      .price(2_000)
      .stock(8)
      .name("My Product")
      .description(
        "While you may start your Spring Boot application very easily from your test (or test suite) itself, it may be desirable to handle that in the build itself. To make sure that the lifecycle of your Spring Boot application is properly managed around your integration tests, you can use the start and stop goals, as shown in the following example"
      )
      .build();

    assertThrows(
      InvalidProductException.class,
      product::validate,
      "La descripcion de un producto no puede tener mas de 200 caracteres"
    );
  }

  @Test
  void productWithNegativePrice() {
    var product = Product.builder()
      .price(-90_000)
      .stock(8)
      .name("My Product")
      .description("shown in the following example")
      .build();

    assertThrows(
      InvalidProductException.class,
      product::validate,
      "El precio del producto debe ser mayor a cero"
    );
  }

  @Test
  void validateProductWithNoDescription() {
    var product = Product.builder()
      .price(90_000)
      .stock(8)
      .name("My Product")
      .build();

    product.validate(); // should not fail
  }
}
