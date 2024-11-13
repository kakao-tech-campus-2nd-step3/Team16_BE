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

    @ExceptionHandler(InvalidTimeRangeException.class)
    public ResponseEntity<FailureBody> handleInvalidTimeRangeException(
        InvalidTimeRangeException ex) {
        return ApiResponseGenerator.fail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(TimeNullException.class)
    public ResponseEntity<FailureBody> handleTimeNullException(TimeNullException ex) {
        return ApiResponseGenerator.fail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(EventDetailNotFoundException.class)
    public ResponseEntity<FailureBody> handleEventDetailNotFoundException(
        EventDetailNotFoundException ex) {
        return ApiResponseGenerator.fail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }
}
