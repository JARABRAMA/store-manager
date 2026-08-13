package com.jarabrama.store_manager.authentication.domain.exceptions;

/**
 * UserNotFoundException
 */
public class UserNotFoundException extends RuntimeException {

  public UserNotFoundException(String message) {
    super(message);
  }
}
