package org.cookieandkakao.babting.common.exception;
import org.cookieandkakao.babting.common.apiresponse.ApiResponseBody.FailureBody;
import org.cookieandkakao.babting.common.apiresponse.ApiResponseGenerator;
import org.cookieandkakao.babting.common.exception.customexception.ApiException;
import org.cookieandkakao.babting.common.exception.customexception.CacheEvictionException;
import org.cookieandkakao.babting.domain.calendar.exception.EventCreationException;
import org.cookieandkakao.babting.domain.member.exception.ExpiredTokenException;
import org.cookieandkakao.babting.domain.food.exception.InvalidFoodPreferenceTypeException;
import org.cookieandkakao.babting.domain.calendar.exception.JsonConversionException;
import org.cookieandkakao.babting.domain.member.exception.MemberNotFoundException;
import org.cookieandkakao.babting.domain.food.exception.FoodNotFoundException;
import org.cookieandkakao.babting.domain.food.exception.PreferenceConflictException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<FailureBody> handleValidationException(MethodArgumentNotValidException ex) {
        return ApiResponseGenerator.fail(HttpStatus.BAD_REQUEST, "유효성 검사 오류");
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<FailureBody> handleApiException(ApiException ex) {
        return ApiResponseGenerator.fail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(CacheEvictionException.class)
    public ResponseEntity<FailureBody> handleCacheEvictionException(CacheEvictionException ex) {
        return ApiResponseGenerator.fail(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }
}
