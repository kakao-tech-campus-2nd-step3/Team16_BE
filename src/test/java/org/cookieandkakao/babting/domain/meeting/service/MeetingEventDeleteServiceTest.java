package org.cookieandkakao.babting.domain.meeting.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import org.cookieandkakao.babting.domain.meeting.entity.MeetingEvent;
import org.cookieandkakao.babting.domain.meeting.entity.MemberMeeting;
import org.cookieandkakao.babting.domain.meeting.repository.MeetingEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;

class MeetingEventDeleteServiceTest {

    @Mock
    private MeetingEventRepository meetingEventRepository;

    @InjectMocks
    private MeetingEventDeleteService meetingEventDeleteService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    class 개인_일정_삭제_테스트 {

        @Test
        void 성공_개인_일정이_있는_경우() {
            // Given
            MemberMeeting memberMeeting = new MemberMeeting(null, null, false);
            MeetingEvent event1 = new MeetingEvent(memberMeeting, null);
            MeetingEvent event2 = new MeetingEvent(memberMeeting, null);
            List<MeetingEvent> existingEvents = List.of(event1, event2);

            // Mocking
            given(meetingEventRepository.findByMemberMeeting(memberMeeting)).willReturn(existingEvents);

            // When
            meetingEventDeleteService.deleteMeetingEvent(memberMeeting);

            // Then
            verify(meetingEventRepository).findByMemberMeeting(memberMeeting);
            verify(meetingEventRepository).deleteAll(existingEvents);
        }

        @Test
        void 성공_개인_일정이_없는_경우() {
            // Given
            MemberMeeting memberMeeting = new MemberMeeting(null, null, false);

            // Mocking
            given(meetingEventRepository.findByMemberMeeting(memberMeeting)).willReturn(List.of());

            // When
            meetingEventDeleteService.deleteMeetingEvent(memberMeeting);

            // Then
            verify(meetingEventRepository, times(1)).deleteAll(List.of());
        }

        @Test
        void 실패_JPA에서_에러_발생() {
            // Given
            MemberMeeting memberMeeting = new MemberMeeting(null, null, false); // 필요한 경우 Member와 Meeting 초기화

            // Mocking
            doThrow(new DataAccessException("Database error") {}).when(meetingEventRepository).findByMemberMeeting(memberMeeting);

            // When & Then
            assertThrows(DataAccessException.class, () -> meetingEventDeleteService.deleteMeetingEvent(memberMeeting));
        }
    }
}