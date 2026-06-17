package com.jarabrama.store_manager.inventory.domain.model;

import lombok.Builder;

import java.util.List;

@Builder
public record DomainPage<T>(
  List<T> content,
  int page,
  int size,
  int totalPages,
  long totalElements,
  boolean last,
  boolean first
) {}
