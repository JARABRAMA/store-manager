package com.jarabrama.store_manager.inventory.application.model.dtos;

import java.util.List;

public record UpdateProductRequest(
  String name,
  String description,
  double price,
  int stock,
  String imageUrl,
  List<String> categories
) {}
