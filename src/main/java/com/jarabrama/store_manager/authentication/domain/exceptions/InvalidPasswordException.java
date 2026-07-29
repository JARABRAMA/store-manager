package com.jarabrama.store_manager.authentication.domain.exceptions;

/**
 * InvalidPasswordException
 */
public class InvalidPasswordException extends RuntimeException {

  public InvalidPasswordException(String message) {
    super(message);
  }
}
