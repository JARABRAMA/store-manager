package com.jarabrama.store_manager.inventory.application;

import com.jarabrama.store_manager.inventory.application.model.dtos.CreateProductRequest;
import com.jarabrama.store_manager.inventory.application.model.dtos.PageResponse;
import com.jarabrama.store_manager.inventory.application.model.dtos.ProductResponse;
import com.jarabrama.store_manager.inventory.application.model.dtos.UpdateProductRequest;
import com.jarabrama.store_manager.inventory.application.model.mappers.CreateProductRequestMapper;
import com.jarabrama.store_manager.inventory.application.model.mappers.ProductResponseMapper;
import com.jarabrama.store_manager.inventory.application.ports.in.ProductUseCase;
import com.jarabrama.store_manager.inventory.domain.exceptions.InvalidProductException;
import com.jarabrama.store_manager.inventory.domain.model.DomainPage;
import com.jarabrama.store_manager.inventory.domain.model.Product;
import com.jarabrama.store_manager.inventory.infraestructure.ports.out.CategoryRepository;
import com.jarabrama.store_manager.inventory.infraestructure.ports.out.ProductRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService implements ProductUseCase {

  private final ProductRepository productRepo;
  private final CategoryRepository categoryRepository;

  private final CreateProductRequestMapper createProductRequestMapper;
  private final ProductResponseMapper productResponseMapper;

  @Override
  @Transactional
  public void saveProduct(CreateProductRequest req) {
    if (productRepo.alreadyExists(req.name())) {
      throw new InvalidProductException(
        "Ya existe otro producto con el mismo nombre"
      );
    }

    var product = createProductRequestMapper.toDomain(req);
    product.setId(UUID.randomUUID());

    product.validate();

    var notAlreadyCreatedCategories = filterNew(req.categories());
    if (!notAlreadyCreatedCategories.isEmpty()) {
      categoryRepository.saveAll(notAlreadyCreatedCategories);
    }

    productRepo.save(product);
  }

  private List<String> filterNew(List<String> categories) {
    if (categories == null) return List.of();

    return categories
      .stream()
      .filter(name -> !categoryRepository.alreadyExists(name))
      .toList();
  }

  @Override
  public PageResponse<ProductResponse> findProducts(
    String search,
    String category,
    Integer page
  ) {
    int pageSize = 10; // defined on requirements
    DomainPage<Product> pageProducts = productRepo.findAll(
      search,
      category,
      page,
      pageSize
    );

    return getPageResponse(pageProducts);
  }

  private PageResponse<ProductResponse> getPageResponse(
    DomainPage<Product> products
  ) {
    return PageResponse.<ProductResponse>builder()
      .content(
        products
          .content()
          .stream()
          .map(productResponseMapper::fromDomain)
          .toList()
      )
      .size(products.size())
      .totalElements(products.totalElements())
      .totalPages(products.totalPages())
      .page(products.page())
      .first(products.first())
      .last(products.last())
      .build();
  }

  @Override
  @Transactional
  public void updateProduct(String id, UpdateProductRequest req) {
    var uuid = createAndValidateUUIDFromString(id);

    if (productRepo.existsByNameWithDifferentId(req.name(), uuid)) {
      throw new InvalidProductException(
        "Ya existe otro producto con ese nombre"
      );
    }

    var persistedProduct = getProduct(uuid);
    persistedProduct = updateProductAttributes(req, persistedProduct);
    persistedProduct.validate();

    var notAlreadyCreatedCategories = filterNew(req.categories());
    if (!notAlreadyCreatedCategories.isEmpty()) {
      categoryRepository.saveAll(notAlreadyCreatedCategories);
    }

    productRepo.save(persistedProduct);
  }

  private Product updateProductAttributes(
    UpdateProductRequest req,
    Product persistedProduct
  ) {
    if (req.name() != null) {
      persistedProduct.setName(req.name());
    }
    if (req.description() != null) {
      persistedProduct.setDescription(req.description());
    }
    if (req.stock() != null) {
      persistedProduct.setStock(req.stock());
    }
    if (req.price() != null) {
      persistedProduct.setPrice(req.price());
    }
    if (req.imageUrl() != null) {
      persistedProduct.setImageUrl(req.imageUrl());
    }
    if (req.categories() != null) {
      persistedProduct.setCategories(req.categories());
    }
    return persistedProduct;
  }

  private UUID createAndValidateUUIDFromString(String rawId) {
    try {
      return UUID.fromString(rawId);
    } catch (IllegalArgumentException ex) {
      log.error(ex.getMessage());
      throw new InvalidProductException("El id de producto es invalido");
    }
  }

  private Product getProduct(UUID id) {
    return productRepo
      .findById(id)
      .orElseThrow(() ->
        new InvalidProductException("El producto no existe en el sistema")
      );
  }

  @Override
  public List<String> fetchAllCategories() {
    return categoryRepository.findAll();
  }
}
