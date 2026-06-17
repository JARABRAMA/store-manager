package com.jarabrama.store_manager.inventory.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.jarabrama.store_manager.inventory.application.model.dtos.CreateProductRequest;
import com.jarabrama.store_manager.inventory.application.model.dtos.PageResponse;
import com.jarabrama.store_manager.inventory.application.model.dtos.ProductResponse;
import com.jarabrama.store_manager.inventory.application.model.mappers.CreateProductRequestMapper;
import com.jarabrama.store_manager.inventory.application.model.mappers.ProductResponseMapper;
import com.jarabrama.store_manager.inventory.domain.exceptions.InvalidProductException;
import com.jarabrama.store_manager.inventory.domain.model.DomainPage;
import com.jarabrama.store_manager.inventory.domain.model.Product;
import com.jarabrama.store_manager.inventory.infraestructure.ports.out.CategoryRepository;
import com.jarabrama.store_manager.inventory.infraestructure.ports.out.ProductRepository;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

  @Mock
  private ProductRepository productRepo;

  @Mock
  private CategoryRepository categoryRepository;

  @Spy
  private CreateProductRequestMapper createProductRequestMapper;

  @Spy
  private ProductResponseMapper productResponseMapper;

  @InjectMocks
  private ProductService productService;

  private CreateProductRequest correctTestRequest;

  @BeforeEach
  void init() {
    correctTestRequest = CreateProductRequest.builder()
      .name("My Product")
      .stock(0)
      .price(1_200)
      .description("Some important details of the product")
      .build();
  }

  @Test
  void saveProductWithValidProductTest() {
    when(productRepo.alreadyExists(correctTestRequest.name())).thenReturn(
      false
    );
    doNothing().when(productRepo).save(any());

    productService.saveProduct(correctTestRequest);
    verify(productRepo).alreadyExists(correctTestRequest.name());
    verify(productRepo).save(any());
  }

  @Test
  void saveProductThatAlreadyExists() {
    when(productRepo.alreadyExists(correctTestRequest.name())).thenReturn(true);

    assertThrows(
      InvalidProductException.class,
      () -> productService.saveProduct(correctTestRequest),
      "Ya existe otro producto con el mismo nombre"
    );

    verify(productRepo).alreadyExists(correctTestRequest.name());
    verify(productRepo, never()).save(any());
  }

  @Test
  void saveProductWithInvalidAttributeValues() {
    var invalidProduct = CreateProductRequest.builder()
      .name("Name with more than 50 characters for testing product service")
      .stock(0)
      .price(1200)
      .build();

    when(productRepo.alreadyExists(invalidProduct.name())).thenReturn(false);

    assertThrows(InvalidProductException.class, () ->
      productService.saveProduct(invalidProduct)
    );

    verify(productRepo).alreadyExists(invalidProduct.name());
  }

  @Test
  void saveProductWithValidProductAndAlreadyCreatedCategoriesTest() {
    var categories = List.of("Category 1", "Category 2");
    var request = CreateProductRequest.builder()
      .name("My Product")
      .stock(0)
      .price(1_200)
      .description("Some important details of the product")
      .categories(categories)
      .build();

    when(categoryRepository.alreadyExists(anyString())).thenReturn(false);
    doNothing().when(productRepo).save(any());

    productService.saveProduct(request);
    verify(categoryRepository).saveAll(categories);
    verify(productRepo).save(any());
  }

  @Test
  void saveProductWithValidProductAndNotAlreadyCreatedCategoriesTest() {
    var categories = List.of("Category 1", "Category 2");
    var request = CreateProductRequest.builder()
      .name("My Product")
      .stock(0)
      .price(1_200)
      .description("Some important details of the product")
      .categories(categories)
      .build();

    when(categoryRepository.alreadyExists(anyString())).thenReturn(true);

    productService.saveProduct(request);

    verify(categoryRepository, times(2)).alreadyExists(anyString());
    verify(productRepo).save(any());
    verify(categoryRepository, never()).saveAll(anyList());
  }

  @Test
  void findProductWithNullSearchValues() {
    var uuid = UUID.randomUUID();
    List<Product> products = List.of(
      Product.builder()
        .name(("producto 1"))
        .id(uuid)
        .build()
    );
    var domainPage = DomainPage.<Product>builder()
      .page(0)
      .size(1)
      .totalPages(1)
      .totalElements(1)
      .content(products)
      .last(true)
      .first(true)
      .build();

    List<ProductResponse> expectedProducts = List.of(
      ProductResponse.builder().name("producto 1").id(uuid.toString()).build()
    );
    var expectedPageResponse = PageResponse.<ProductResponse>builder()
      .page(0)
      .size(1)
      .totalPages(1)
      .totalElements(1)
      .content(expectedProducts)
      .last(true)
      .first(true)
      .build();

    when(productRepo.findAll(null, null, 0, 10)).thenReturn(domainPage);

    var actualPageResponse = productService.findProducts(null, null, 0);

    verify(productRepo).findAll(null, null, 0, 10);
    assertEquals(expectedPageResponse, actualPageResponse);
  }

  @Test
  void findProductsWithNotNullValues() {
    var uuid = UUID.randomUUID();
    List<Product> products = List.of(
      Product.builder()
        .name(("producto 1"))
        .id(uuid)
        .build()
    );
    var domainPage = DomainPage.<Product>builder()
      .page(0)
      .size(1)
      .totalPages(1)
      .totalElements(1)
      .content(products)
      .last(true)
      .first(true)
      .build();

    List<ProductResponse> expectedProducts = List.of(
      ProductResponse.builder().name("producto 1").id(uuid.toString()).build()
    );
    var expectedPageResponse = PageResponse.<ProductResponse>builder()
      .page(0)
      .size(1)
      .totalPages(1)
      .totalElements(1)
      .content(expectedProducts)
      .last(true)
      .first(true)
      .build();

    when(productRepo.findAll("producto", "categoria 1", 0, 10)).thenReturn(
      domainPage
    );

    var actualPageResponse = productService.findProducts(
      "producto",
      "categoria 1",
      0
    );

    verify(productRepo).findAll("producto", "categoria 1", 0, 10);
    assertEquals(expectedPageResponse, actualPageResponse);
  }
}
