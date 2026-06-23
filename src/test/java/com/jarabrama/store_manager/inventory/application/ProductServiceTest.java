package com.jarabrama.store_manager.inventory.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.jarabrama.store_manager.inventory.application.model.dtos.CreateProductRequest;
import com.jarabrama.store_manager.inventory.application.model.dtos.PageResponse;
import com.jarabrama.store_manager.inventory.application.model.dtos.ProductResponse;
import com.jarabrama.store_manager.inventory.application.model.dtos.UpdateProductRequest;
import com.jarabrama.store_manager.inventory.application.model.mappers.CreateProductRequestMapper;
import com.jarabrama.store_manager.inventory.application.model.mappers.ProductResponseMapper;
import com.jarabrama.store_manager.inventory.application.model.mappers.UpdateProductRequestMapper;
import com.jarabrama.store_manager.inventory.domain.exceptions.InvalidProductException;
import com.jarabrama.store_manager.inventory.domain.model.DomainPage;
import com.jarabrama.store_manager.inventory.domain.model.Product;
import com.jarabrama.store_manager.inventory.infraestructure.ports.out.CategoryRepository;
import com.jarabrama.store_manager.inventory.infraestructure.ports.out.ProductRepository;
import jakarta.persistence.Id;
import java.util.List;
import java.util.Optional;
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
  private UpdateProductRequestMapper updateProductRequestMapper;

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

  @Test
  void updateProductWithNameThatAlreadyExists() {
    var uuid = UUID.randomUUID();
    var product = UpdateProductRequest.builder().name("producto 1").build();

    when(
      productRepo.existsByNameWithDifferentId(product.name(), uuid)
    ).thenReturn(true);

    assertThrows(
      InvalidProductException.class,
      () -> productService.updateProduct(uuid.toString(), product),
      "Ya existe otro producto con ese nombre"
    );

    verify(productRepo).existsByNameWithDifferentId(product.name(), uuid);
  }

  @Test
  void updateProductWithMalformedId() {
    var id = "malformed id";
    var invalidProduct = UpdateProductRequest.builder()
      .name("product 1")
      .stock(40)
      .build();

    assertThrows(
      InvalidProductException.class,
      () -> productService.updateProduct(id, invalidProduct),
      "El id de producto es invalido"
    );

    verify(productRepo, never()).existsByNameWithDifferentId(
      anyString(),
      any()
    );
  }

  @Test
  void updateProductWithInvalidAttributes() {
    var id = UUID.randomUUID();
    var invalidProduct = UpdateProductRequest.builder()
      .name("name with more than 50 characters to make it fails")
      .stock(-3)
      .build(); // invalid negative stock

    when(
      productRepo.existsByNameWithDifferentId(invalidProduct.name(), id)
    ).thenReturn(false);

    assertThrows(InvalidProductException.class, () ->
      productService.updateProduct(id.toString(), invalidProduct)
    );

    verify(productRepo).existsByNameWithDifferentId(invalidProduct.name(), id);
  }

  @Test
  void updateProductWithIdThatDoesNotExists() {
    var id = UUID.randomUUID();
    var product = UpdateProductRequest.builder()
      .name("product 1")
      .stock(40)
      .price(Double.valueOf(3_500))
      .build();

    when(
      productRepo.existsByNameWithDifferentId(product.name(), id)
    ).thenReturn(false);
    when(productRepo.findById(id)).thenReturn(Optional.empty());

    assertThrows(
      InvalidProductException.class,
      () -> productService.updateProduct(id.toString(), product),
      "El producto no existe en el sistema"
    );

    verify(productRepo).existsByNameWithDifferentId(anyString(), any());
    verify(productRepo).findById(any());
  }

  @Test
  void updateProductWhitNonExistentCategories() {
    var id = UUID.randomUUID();
    var categories = List.of("category 1", "category 2");
    var product = UpdateProductRequest.builder()
      .name("product 1")
      .description("description of the product")
      .stock(40)
      .price(Double.valueOf(3_500))
      .categories(categories)
      .build();

    var productPersisted = Product.builder()
      .name("product 1")
      .description("description of the product")
      .stock(40)
      .price(3_500)
      .categories(categories)
      .build();

    when(categoryRepository.alreadyExists(anyString())).thenReturn(false);
    when(
      productRepo.existsByNameWithDifferentId(product.name(), id)
    ).thenReturn(false);
    when(productRepo.findById(id)).thenReturn(
      Optional.<Product>of(productPersisted)
    );

    verify(productRepo).existsByNameWithDifferentId(product.name(), id);
    verify(productRepo).findById(any());
    verify(categoryRepository).saveAll(categories);
  }
}
