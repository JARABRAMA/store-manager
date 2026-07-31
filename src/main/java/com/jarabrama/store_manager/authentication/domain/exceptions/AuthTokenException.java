package com.jarabrama.store_manager.authentication.domain.exceptions;

public class AuthTokenException extends RuntimeException {
  public AuthTokenException(String message) {
    super(message);
  }
}
