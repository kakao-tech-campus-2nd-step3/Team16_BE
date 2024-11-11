package org.cookieandkakao.babting.domain.meeting.exception;

import org.cookieandkakao.babting.common.apiresponse.ApiResponseBody.FailureBody;
import org.cookieandkakao.babting.common.apiresponse.ApiResponseGenerator;
import org.cookieandkakao.babting.domain.meeting.exception.meeting.MeetingAlreadyConfirmedException;
import org.cookieandkakao.babting.domain.meeting.exception.meeting.MeetingAlreadyJoinException;
import org.cookieandkakao.babting.domain.meeting.exception.meeting.MeetingHostUnauthorizedException;
import org.cookieandkakao.babting.domain.meeting.exception.meeting.MeetingNotFoundException;
import org.cookieandkakao.babting.domain.meeting.exception.membermeeting.MemberMeetingNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class MeetingExceptionHandler {
    @ExceptionHandler(MeetingNotFoundException.class)
    public ResponseEntity<FailureBody> handleMeetingNotFoundException(MeetingNotFoundException ex) {
        return ApiResponseGenerator.fail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(MemberMeetingNotFoundException.class)
    public ResponseEntity<FailureBody> handleMemberMeetingNotFoundException(MemberMeetingNotFoundException ex) {
        return ApiResponseGenerator.fail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(MeetingAlreadyJoinException.class)
    public ResponseEntity<FailureBody> handleMemberMeetingAlreadyJoinException(MeetingAlreadyJoinException ex) {
        return ApiResponseGenerator.fail(HttpStatus.NOT_ACCEPTABLE, ex.getMessage());
    }

    @ExceptionHandler(MeetingHostUnauthorizedException.class)
    public ResponseEntity<FailureBody> handleMeetingHostUnauthorizedException(MeetingHostUnauthorizedException ex) {
        return ApiResponseGenerator.fail(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(MeetingAlreadyConfirmedException.class)
    public ResponseEntity<FailureBody> handleMeetingAlreadyConfirmedException(MeetingAlreadyConfirmedException ex) {
        return ApiResponseGenerator.fail(HttpStatus.NOT_ACCEPTABLE, ex.getMessage());
    }
}
