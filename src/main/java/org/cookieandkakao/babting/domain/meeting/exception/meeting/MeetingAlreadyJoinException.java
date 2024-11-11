package org.cookieandkakao.babting.domain.meeting.exception.meeting;

public class MeetingAlreadyJoinException extends IllegalStateException{
    public MeetingAlreadyJoinException(String message) {
        super(message);
    }
}
