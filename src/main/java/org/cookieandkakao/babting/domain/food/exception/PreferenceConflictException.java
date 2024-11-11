package org.cookieandkakao.babting.domain.food.exception;

public class PreferenceConflictException extends RuntimeException {
  public PreferenceConflictException(String message) {
    super(message);
  }
}
