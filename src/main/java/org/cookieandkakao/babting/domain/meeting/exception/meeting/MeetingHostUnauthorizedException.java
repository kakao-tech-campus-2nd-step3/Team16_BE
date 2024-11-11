package org.cookieandkakao.babting.domain.meeting.exception.meeting;

public class MeetingHostUnauthorizedException extends IllegalStateException{
    public MeetingHostUnauthorizedException(String message) {
        super(message);
    }
}
