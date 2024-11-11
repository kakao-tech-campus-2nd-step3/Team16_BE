package org.cookieandkakao.babting.domain.meeting.exception.meeting;

public class MeetingAlreadyConfirmedException extends IllegalStateException{
    public MeetingAlreadyConfirmedException(String message) {
        super(message);
    }
}
