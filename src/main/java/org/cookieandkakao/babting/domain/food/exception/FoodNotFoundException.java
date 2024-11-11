package org.cookieandkakao.babting.domain.food.exception;

public class FoodNotFoundException extends RuntimeException {
  public FoodNotFoundException(String message) {
    super(message);
  }
}
