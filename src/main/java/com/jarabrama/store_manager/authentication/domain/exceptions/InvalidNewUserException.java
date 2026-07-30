package com.jarabrama.store_manager.authentication.domain.exceptions;

public class InvalidNewUserException extends RuntimeException {
  public InvalidNewUserException(String message) {
    super(message);
  }
}
