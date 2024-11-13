package org.cookieandkakao.babting.domain.meeting.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.List;
import org.cookieandkakao.babting.domain.food.exception.PreferenceConflictException;
import org.cookieandkakao.babting.domain.food.service.MeetingFoodPreferenceUpdater;
import org.cookieandkakao.babting.domain.meeting.dto.request.MeetingJoinCreateRequest;
import org.cookieandkakao.babting.domain.meeting.exception.meeting.MeetingAlreadyJoinException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class MeetingJoinServiceTest {

    @Mock
    private MeetingService meetingService;

    @Mock
    private MeetingFoodPreferenceUpdater meetingFoodPreferenceUpdater;

    @InjectMocks
    private MeetingJoinService meetingJoinService;

    private static final Long MEMBER_ID = 1L;
    private static final Long MEETING_ID = 2L;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    class 모임_참가_테스트 {

        @Test
        void 성공() {
            // Given
            MeetingJoinCreateRequest meetingJoinCreateRequest = mock(
                MeetingJoinCreateRequest.class);

            // When & Then
            assertDoesNotThrow(() -> meetingJoinService.joinMeeting(MEMBER_ID, MEETING_ID,
                meetingJoinCreateRequest));
            verify(meetingService).joinMeeting(MEMBER_ID, MEETING_ID, meetingJoinCreateRequest);
            verify(meetingFoodPreferenceUpdater).updatePreferences(
                MEETING_ID, MEMBER_ID, meetingJoinCreateRequest.preferences(),
                meetingJoinCreateRequest.nonPreferences());
        }

        @Test
        void 실패_이미_참가한_경우() {
            // Given
            MeetingJoinCreateRequest meetingJoinCreateRequest = mock(
                MeetingJoinCreateRequest.class);

            // Mocking
            doThrow(new MeetingAlreadyJoinException("이미 모임에 참가한 상태입니다.")).when(meetingService)
                .joinMeeting(MEMBER_ID, MEETING_ID, meetingJoinCreateRequest);

            // When
            Exception e = assertThrows(MeetingAlreadyJoinException.class,
                () -> meetingJoinService.joinMeeting(MEMBER_ID, MEETING_ID,
                    meetingJoinCreateRequest));

            // Then
            assertEquals(e.getClass(), MeetingAlreadyJoinException.class);
            assertEquals(e.getMessage(), "이미 모임에 참가한 상태입니다.");
            verify(meetingService).joinMeeting(MEMBER_ID, MEETING_ID, meetingJoinCreateRequest);
        }

        @Test
        void 실패_선호도_업데이트_실패한_경우() {
            // Given
            MeetingJoinCreateRequest meetingJoinCreateRequest = mock(
                MeetingJoinCreateRequest.class);
            List<Long> preferences = List.of(1L, 2L);
            List<Long> nonPreferences = List.of(3L, 4L);

            given(meetingJoinCreateRequest.preferences()).willReturn(preferences);
            given(meetingJoinCreateRequest.nonPreferences()).willReturn(nonPreferences);

            doThrow(new PreferenceConflictException("선호도 업데이트 중 오류가 발생했습니다."))
                .when(meetingFoodPreferenceUpdater).updatePreferences(
                    MEETING_ID, MEMBER_ID, preferences, nonPreferences);

            // When
            Exception e = assertThrows(PreferenceConflictException.class,
                () -> meetingJoinService.joinMeeting(MEMBER_ID, MEETING_ID,
                    meetingJoinCreateRequest));

            // Then
            assertEquals(e.getClass(), PreferenceConflictException.class);
            assertEquals(e.getMessage(), "선호도 업데이트 중 오류가 발생했습니다.");
            verify(meetingService).joinMeeting(MEMBER_ID, MEETING_ID, meetingJoinCreateRequest);
            verify(meetingFoodPreferenceUpdater).updatePreferences(MEETING_ID, MEMBER_ID,
                meetingJoinCreateRequest.preferences(), meetingJoinCreateRequest.nonPreferences());

        }
    }

}