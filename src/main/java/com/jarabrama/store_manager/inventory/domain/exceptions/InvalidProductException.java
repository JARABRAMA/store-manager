package com.jarabrama.store_manager.inventory.domain.exceptions;

public class InvalidProductException extends RuntimeException {

  public InvalidProductException(String message) {
    super(message);
  }
}
