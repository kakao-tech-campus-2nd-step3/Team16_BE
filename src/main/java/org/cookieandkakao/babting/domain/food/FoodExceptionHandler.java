package org.cookieandkakao.babting.domain.food;

import org.cookieandkakao.babting.common.apiresponse.ApiResponseBody.FailureBody;
import org.cookieandkakao.babting.common.apiresponse.ApiResponseGenerator;
import org.cookieandkakao.babting.domain.food.exception.FoodNotFoundException;
import org.cookieandkakao.babting.domain.food.exception.InvalidFoodPreferenceTypeException;
import org.cookieandkakao.babting.domain.food.exception.PreferenceConflictException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class FoodExceptionHandler {

    @ExceptionHandler(PreferenceConflictException.class)
    public ResponseEntity<FailureBody> handlePreferenceConflictException(PreferenceConflictException ex) {
        return ApiResponseGenerator.fail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(InvalidFoodPreferenceTypeException.class)
    public ResponseEntity<FailureBody> handleInvalidFoodPreferenceTypeException(InvalidFoodPreferenceTypeException ex) {
        return ApiResponseGenerator.fail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(FoodNotFoundException.class)
    public ResponseEntity<FailureBody> handleFoodNotFoundException(FoodNotFoundException ex) {
        return ApiResponseGenerator.fail(HttpStatus.NOT_FOUND, ex.getMessage());
    }
}
