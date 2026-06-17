package com.jarabrama.store_manager.inventory.application.model.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

@Builder
public record CreateProductRequest(
  @NotBlank(message = "El nombre del producto es requerido")
  @Size(
    max = 50,
    message = "El nombre de un producto no puede tener mas de 50 caracteres"
  )
  String name,

  @PositiveOrZero(
    message = "El precio de un producto debe ser mayor o igual a cero"
  )
  double price,

  @PositiveOrZero(
    message = "Las unidades disponibles deben ser mayor o igual a cero"
  )
  int stock,

  @Size(
    max = 100,
    message = "La descripcion del producto no puede tener mas de 100 caracteres"
  )
  String description,
  List<String> categories,
  String imageUrl,
  LocalDate expiresAt
) {}
