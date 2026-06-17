package com.jarabrama.store_manager.inventory.domain.model;

import com.jarabrama.store_manager.inventory.domain.exceptions.InvalidProductException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Product {

  private UUID id;
  private String name;
  private String description;
  private double price;
  private int stock;
  private LocalDate expiresAt;
  private String imageUrl;
  private List<String> categories;

  public boolean isInStock() {
    return stock > 0;
  }

  public void validate() {
    if (name == null || name.isBlank()) {
      throw new InvalidProductException("El nombre del producto es requerido");
    }
    if (price < 0) {
      throw new InvalidProductException(
        "El precio del producto debe ser mayor a cero"
      );
    }
    if (stock < 0) {
      throw new InvalidProductException(
        "Las unidades disponibles no pueden ser menores a cero"
      );
    }
    if (name.length() > 50) {
      throw new InvalidProductException(
        "El nombre de un producto no puede tener mas de 50 caracteres"
      );
    }
    if (description != null && description.length() > 100) {
      throw new InvalidProductException(
        "La descripcion de un producto no puede tener mas de 200 caracteres"
      );
    }
  }
}
