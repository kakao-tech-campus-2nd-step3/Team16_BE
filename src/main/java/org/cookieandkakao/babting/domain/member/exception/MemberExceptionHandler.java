package org.cookieandkakao.babting.domain.member.exception;

import org.cookieandkakao.babting.common.apiresponse.ApiResponseBody.FailureBody;
import org.cookieandkakao.babting.common.apiresponse.ApiResponseGenerator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class MemberExceptionHandler {

    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<FailureBody> handleMemberNotFoundException(MemberNotFoundException ex) {
        return ApiResponseGenerator.fail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(ExpiredTokenException.class)
    public ResponseEntity<FailureBody> handleExpiredTokenException(ExpiredTokenException ex) {
        return ApiResponseGenerator.fail(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }
}
