package org.cookieandkakao.babting.domain.meeting.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.List;
import org.cookieandkakao.babting.common.apiresponse.ApiResponseBody.SuccessBody;
import org.cookieandkakao.babting.common.exception.customexception.ApiException;
import org.cookieandkakao.babting.domain.calendar.dto.response.TimeGetResponse;
import org.cookieandkakao.babting.domain.meeting.dto.request.ConfirmMeetingGetRequest;
import org.cookieandkakao.babting.domain.meeting.dto.request.MeetingJoinCreateRequest;
import org.cookieandkakao.babting.domain.meeting.dto.request.MeetingTimeCreateRequest;
import org.cookieandkakao.babting.domain.meeting.dto.response.MeetingPersonalEventGetResponse;
import org.cookieandkakao.babting.domain.meeting.service.MeetingEventService;
import org.cookieandkakao.babting.domain.meeting.service.MeetingJoinService;
import org.cookieandkakao.babting.domain.meeting.service.MeetingService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class MeetingControllerTest {

    @Mock
    private MeetingService meetingService;

    @Mock
    private MeetingEventService meetingEventService;

    @Mock
    private MeetingJoinService meetingJoinService;

    @InjectMocks
    private MeetingController meetingController;

    private static final Long MEMBER_ID = 1L;
    private static final Long MEETING_ID = 2L;

    @Test
    void 모임의_시작_시간은_현재_시간보다_빠를_수_없다() {
        //given
        //when
        //then
    }

    @Test
    void 모임의_모든_정보가_입력되어야한다() {
        //given
        //when
        //then
    }

    @Nested
    class 모임_참가_테스트 {

        @Test
        void 성공() {
            // Given
            List<Long> preferences = List.of(1L, 2L);
            List<Long> nonPreferences = List.of(3L, 4L);
            List<MeetingTimeCreateRequest> times = List.of(
                new MeetingTimeCreateRequest("2024-01-01T10:00:00Z", "2024-01-01T12:00:00Z",
                    "Asia/Seoul", false));
            MeetingJoinCreateRequest meetingJoinCreateRequest = new MeetingJoinCreateRequest(
                preferences, nonPreferences, times);

            // When
            ResponseEntity<SuccessBody<Void>> result = meetingController.joinMeeting(MEETING_ID,
                MEMBER_ID, meetingJoinCreateRequest);

            // Then
            verify(meetingJoinService).joinMeeting(eq(MEMBER_ID), eq(MEETING_ID),
                any(MeetingJoinCreateRequest.class));
            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(result.getBody().getMessage()).isEqualTo("모임 참가 성공");
        }

        @Test
        void 실패_ApiException예외() {
            // Given
            List<Long> preferences = List.of(1L, 2L);
            List<Long> nonPreferences = List.of(3L, 4L);
            List<MeetingTimeCreateRequest> times = List.of(
                new MeetingTimeCreateRequest("2024-01-01T10:00:00Z", "2024-01-01T12:00:00Z",
                    "Asia/Seoul", false));
            MeetingJoinCreateRequest meetingJoinCreateRequest = new MeetingJoinCreateRequest(
                preferences, nonPreferences, times);

            // Mocking
            doThrow(new ApiException("모임 참가 중 오류가 발생했습니다."))
                .when(meetingJoinService)
                .joinMeeting(eq(MEMBER_ID), eq(MEETING_ID), any(MeetingJoinCreateRequest.class));

            // When
            Exception e = assertThrows(ApiException.class,
                () -> meetingController.joinMeeting(MEETING_ID, MEMBER_ID,
                    meetingJoinCreateRequest));

            // Then
            assertThat(e.getClass()).isEqualTo(ApiException.class);
            assertThat(e.getMessage()).isEqualTo("모임 참가 중 오류가 발생했습니다.");
        }
    }

    @Nested
    class 모임_확정_테스트 {

        @Test
        void 성공() {
            // Given
            ConfirmMeetingGetRequest confirmMeetingGetRequest = new ConfirmMeetingGetRequest(
                LocalDateTime.of(2024, 1, 1, 10, 0), 1L);

            // When
            ResponseEntity<SuccessBody<Void>> result = meetingController.confirmMeeting(MEETING_ID,
                confirmMeetingGetRequest, MEMBER_ID);

            // Then
            verify(meetingEventService).confirmMeeting(MEMBER_ID, MEETING_ID,
                confirmMeetingGetRequest);
            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
            assertThat(result.getBody().getMessage()).isEqualTo("모임 확정 성공");
        }

        @Test
        void 실패_ApiException예외() {
            // Given
            ConfirmMeetingGetRequest confirmMeetingGetRequest = new ConfirmMeetingGetRequest(
                LocalDateTime.of(2024, 1, 1, 10, 0), 1L);

            // Mocking
            doThrow(new ApiException("모임 확정 중 오류가 발생했습니다."))
                .when(meetingEventService)
                .confirmMeeting(eq(MEMBER_ID), eq(MEETING_ID), any(ConfirmMeetingGetRequest.class));

            // When
            Exception e = assertThrows(ApiException.class,
                () -> meetingController.confirmMeeting(MEETING_ID, confirmMeetingGetRequest,
                    MEMBER_ID));

            // Then
            assertThat(e.getClass()).isEqualTo(ApiException.class);
            assertThat(e.getMessage()).isEqualTo("모임 확정 중 오류가 발생했습니다.");
        }
    }

    @Nested
    class 모임별_개인_일정_조회_테스트 {

        @Test
        void 성공() {
            // Given
            TimeGetResponse timeGetResponse = new TimeGetResponse(
                "2024-01-01T10:00:00", "2024-01-01T12:00:00", "Asia/Seoul", false);
            MeetingPersonalEventGetResponse response = new MeetingPersonalEventGetResponse(
                List.of(timeGetResponse));

            // Mocking
            given(meetingEventService.findMeetingPersonalEvent(MEETING_ID, MEMBER_ID)).willReturn(
                response);

            // When
            ResponseEntity<SuccessBody<MeetingPersonalEventGetResponse>> result = meetingController.getPersonalEvent(
                MEETING_ID, MEMBER_ID);

            // Then
            verify(meetingEventService).findMeetingPersonalEvent(MEETING_ID, MEMBER_ID);
            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(
                result.getBody().getData().meetingPersonalTimes().get(0).startAt()).isEqualTo(
                "2024-01-01T10:00:00");
            assertThat(result.getBody().getMessage()).isEqualTo("모임별 개인 일정 조회 성공");
        }

        @Test
        void 실패_일정_조회_오류() {
            // Mocking
            given(meetingEventService.findMeetingPersonalEvent(MEETING_ID, MEMBER_ID))
                .willThrow(new ApiException("개인 일정 조회 중 오류가 발생했습니다."));

            // When
            Exception e = assertThrows(ApiException.class,
                () -> meetingController.getPersonalEvent(MEETING_ID, MEMBER_ID));

            // Then
            verify(meetingEventService).findMeetingPersonalEvent(MEETING_ID, MEMBER_ID);
            assertThat(e.getClass()).isEqualTo(ApiException.class);
            assertThat(e.getMessage()).isEqualTo("개인 일정 조회 중 오류가 발생했습니다.");
        }
    }
}