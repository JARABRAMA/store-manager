package com.jarabrama.store_manager.inventory.infraestructure.web.controllers;

import com.jarabrama.store_manager.inventory.application.model.dtos.CreateProductRequest;
import com.jarabrama.store_manager.inventory.application.model.dtos.PageResponse;
import com.jarabrama.store_manager.inventory.application.model.dtos.ProductResponse;
import com.jarabrama.store_manager.inventory.application.model.dtos.SimpleResponse;
import com.jarabrama.store_manager.inventory.application.ports.in.ProductUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

  private final ProductUseCase productService;

  @PostMapping()
  public ResponseEntity<SimpleResponse> save(
    @RequestBody CreateProductRequest req
  ) {
    productService.saveProduct(req);
    return new ResponseEntity<>(
      new SimpleResponse("Producto guardado exitosamente"),
      HttpStatus.CREATED
    );
  }

  @GetMapping
  public ResponseEntity<PageResponse<ProductResponse>> findAll(
    @RequestParam(required = false) String text,
    @RequestParam(required = false) String category,
    @RequestParam(required = false, defaultValue = "0") Integer page
  ) {
    return ResponseEntity.ok(productService.findProducts(text, category, page));
  }
}
