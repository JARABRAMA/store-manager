package com.jarabrama.store_manager.inventory.infraestructure.web.exception;

import com.jarabrama.store_manager.inventory.application.model.dtos.ErrorResponse;
import com.jarabrama.store_manager.inventory.domain.exceptions.InvalidProductException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ProductExceptionHandler {

  @ExceptionHandler(InvalidProductException.class)
  public ResponseEntity<ErrorResponse> handleInvalidProductException(
    InvalidProductException ex
  ) {
    ErrorResponse errorResponse = new ErrorResponse(
      HttpStatus.BAD_REQUEST.value(),
      ex.getMessage()
    );
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }
}
