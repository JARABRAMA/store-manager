package com.jarabrama.store_manager.inventory.application.model.dtos;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Builder;

@Builder
public record UpdateProductRequest(
  @Size(
    max = 50,
    message = "El nombre de un producto no puede tener mas de 50 caracteres"
  )
  String name,

  @Size(
    max = 100,
    message = "La descripcion del producto no puede tener mas de 100 caracteres"
  )
  String description,

  @PositiveOrZero(
    message = "El precio de un producto debe ser mayor o igual a cero"
  )
  Double price,

  @PositiveOrZero(
    message = "Las unidades disponibles deben ser mayor o igual a cero"
  )
  Integer stock,

  String imageUrl,
  List<String> categories
) {}
