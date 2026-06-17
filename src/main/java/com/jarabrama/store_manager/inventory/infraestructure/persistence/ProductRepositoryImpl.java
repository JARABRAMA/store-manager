package com.jarabrama.store_manager.inventory.infraestructure.persistence;

import static com.jarabrama.store_manager.inventory.infraestructure.persistence.jpa.specifications.ProductSpecifications.hasCategory;
import static com.jarabrama.store_manager.inventory.infraestructure.persistence.jpa.specifications.ProductSpecifications.hasSearchTerm;

import com.jarabrama.store_manager.inventory.domain.model.DomainPage;
import com.jarabrama.store_manager.inventory.domain.model.Product;
import com.jarabrama.store_manager.inventory.infraestructure.persistence.entities.ProductEntity;
import com.jarabrama.store_manager.inventory.infraestructure.persistence.entities.mappers.ProductEntityMapper;
import com.jarabrama.store_manager.inventory.infraestructure.persistence.jpa.CategoryJpaRepo;
import com.jarabrama.store_manager.inventory.infraestructure.persistence.jpa.ProductJpaRepo;
import com.jarabrama.store_manager.inventory.infraestructure.ports.out.ProductRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Repository
@Slf4j
public class ProductRepositoryImpl implements ProductRepository {

  private final ProductJpaRepo productRepo;
  private final CategoryJpaRepo categoryRepo;

  private final ProductEntityMapper productMapper;

  @Override
  @Transactional
  public void save(Product product) {
    var entity = productMapper.toEntity(product);

    final var now = LocalDateTime.now();
    final var categories = categoryRepo.findAllByNameIn(
      product.getCategories()
    );
    entity.setCategories(categories);
    entity.setCreatedAt(now);
    entity.setUpdatedAt(now);

    productRepo.save(entity);
  }

  @Override
  public boolean alreadyExists(String name) {
    return productRepo.existsByName(name);
  }

  @Override
  public DomainPage<Product> findAll(
    String search,
    String category,
    int pageNumber,
    int pageSize
  ) {
    Specification<ProductEntity> spec = Specification.where(
      hasCategory(category)
    ).and(hasSearchTerm(search));

    var sort = Sort.by("createdAt").ascending();

    Page<ProductEntity> page = productRepo.findAll(
      spec,
      PageRequest.of(pageNumber, pageSize, sort)
    );

    return buildDomainPage(page);
  }

  private DomainPage<Product> buildDomainPage(Page<ProductEntity> page) {
    return DomainPage.<Product>builder()
      .content(page.getContent().stream().map(productMapper::toDomain).toList())
      .page(page.getNumber())
      .size(page.getSize())
      .totalPages(page.getTotalPages())
      .totalElements(page.getTotalElements())
      .last(page.isLast())
      .first(page.isFirst())
      .build();
  }

  @Override
  public Optional<Product> findById(UUID id) {
    return productRepo.findById(id).map(productMapper::toDomain);
  }

  @Override
  public boolean existsByNameWithDifferentId(String name, UUID id) {
    return productRepo.existsByNameWithDifferentId(name, id);
  }
}
