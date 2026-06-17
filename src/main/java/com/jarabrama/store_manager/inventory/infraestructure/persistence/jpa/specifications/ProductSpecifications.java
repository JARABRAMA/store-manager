package com.jarabrama.store_manager.inventory.infraestructure.persistence.jpa.specifications;

import com.jarabrama.store_manager.inventory.infraestructure.persistence.entities.CategoryEntity;
import com.jarabrama.store_manager.inventory.infraestructure.persistence.entities.ProductEntity;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpecifications {

  public static Specification<ProductEntity> hasSearchTerm(String search) {
    if (search == null || search.isBlank()) return Specification.unrestricted();
    var pattern = ("%" + search + "%").toLowerCase();
    return (root, query, cb) ->
      cb.or(
        cb.like(cb.lower(root.get("name")), pattern),
        cb.like(cb.lower(root.get("description")), pattern)
      );
  }

  public static Specification<ProductEntity> hasCategory(String category) {
    if (
      category == null || category.isBlank()
    ) return Specification.unrestricted();
    return (root, query, cb) -> {
      Join<ProductEntity, CategoryEntity> join = root.join("categories");
      return cb.equal(cb.lower(join.get("name")), category.toLowerCase());
    };
  }
}
