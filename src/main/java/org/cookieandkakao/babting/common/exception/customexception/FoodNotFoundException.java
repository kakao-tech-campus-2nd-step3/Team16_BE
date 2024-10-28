package org.cookieandkakao.babting.common.exception.customexception;

public class FoodNotFoundException extends RuntimeException {
  public FoodNotFoundException(String message) {
    super(message);
  }
}
