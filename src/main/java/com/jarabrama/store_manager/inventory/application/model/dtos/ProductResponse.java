package com.jarabrama.store_manager.inventory.application.model.dtos;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

@Builder
public record ProductResponse(
  String id,
  String name,
  String description,
  double price,
  int stock,
  String imageUrl,
  LocalDate expireAt,
  List<String> categories
) {}
