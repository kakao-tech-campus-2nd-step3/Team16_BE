package org.cookieandkakao.babting.domain.calendar.exception;

import org.cookieandkakao.babting.common.apiresponse.ApiResponseBody.FailureBody;
import org.cookieandkakao.babting.common.apiresponse.ApiResponseGenerator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CalendarExceptionHandler {

    @ExceptionHandler(EventCreationException.class)
    public ResponseEntity<FailureBody> handleEventCreationException(EventCreationException ex) {
        return ApiResponseGenerator.fail(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    @ExceptionHandler(JsonConversionException.class)
    public ResponseEntity<FailureBody> handleJsonConversionException(JsonConversionException ex) {
        return ApiResponseGenerator.fail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }
}
