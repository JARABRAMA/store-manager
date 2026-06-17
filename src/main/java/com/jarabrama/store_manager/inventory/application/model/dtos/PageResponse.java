package com.jarabrama.store_manager.inventory.application.model.dtos;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PageResponse<T> {

  private List<T> content;
  private int page;
  private int size;
  private long totalElements;
  private int totalPages;
  private boolean first;
  private boolean last;
}
