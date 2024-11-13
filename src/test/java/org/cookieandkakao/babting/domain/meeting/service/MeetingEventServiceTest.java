package org.cookieandkakao.babting.domain.meeting.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.List;
import org.cookieandkakao.babting.domain.calendar.dto.response.TimeGetResponse;
import org.cookieandkakao.babting.domain.calendar.entity.Event;
import org.cookieandkakao.babting.domain.calendar.entity.Time;
import org.cookieandkakao.babting.domain.food.exception.FoodNotFoundException;
import org.cookieandkakao.babting.domain.food.service.FoodRepositoryService;
import org.cookieandkakao.babting.domain.meeting.dto.request.ConfirmMeetingGetRequest;
import org.cookieandkakao.babting.domain.meeting.dto.request.MeetingEventCreateRequest;
import org.cookieandkakao.babting.domain.meeting.dto.response.MeetingPersonalEventGetResponse;
import org.cookieandkakao.babting.domain.meeting.entity.Meeting;
import org.cookieandkakao.babting.domain.meeting.entity.MeetingEvent;
import org.cookieandkakao.babting.domain.meeting.entity.MemberMeeting;
import org.cookieandkakao.babting.domain.meeting.exception.meeting.MeetingAlreadyConfirmedException;
import org.cookieandkakao.babting.domain.meeting.exception.meeting.MeetingHostUnauthorizedException;
import org.cookieandkakao.babting.domain.meeting.exception.meeting.MeetingNotFoundException;
import org.cookieandkakao.babting.domain.meeting.exception.membermeeting.MemberMeetingNotFoundException;
import org.cookieandkakao.babting.domain.meeting.repository.MeetingEventRepository;
import org.cookieandkakao.babting.domain.member.entity.Member;
import org.cookieandkakao.babting.domain.member.exception.MemberNotFoundException;
import org.cookieandkakao.babting.domain.member.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class MeetingEventServiceTest {

    @Mock
    private MemberService memberService;
    @Mock
    private MeetingService meetingService;
    @Mock
    private MeetingValidationService meetingValidationService;
    @Mock
    private MeetingEventCreateService meetingEventCreateService;
    @Mock
    private FoodRepositoryService foodRepositoryService;
    @Mock
    private MeetingEventRepository meetingEventRepository;

    @InjectMocks
    private MeetingEventService meetingEventService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    class 모임_확정_테스트 {

        @Test
        void 성공() {
            // Given
            Long memberId = 1L;
            Long meetingId = 2L;
            Long foodId = 3L;

            Member member = mock(Member.class);
            Meeting meeting = mock(Meeting.class);
            ConfirmMeetingGetRequest confirmMeetingGetRequest = mock(
                ConfirmMeetingGetRequest.class);

            LocalDateTime confirmDateTime = LocalDateTime.now();

            // Mocking
            given(memberService.findMember(memberId)).willReturn(member);
            given(meetingService.findMeeting(meetingId)).willReturn(meeting);
            given(confirmMeetingGetRequest.confirmFoodId()).willReturn(foodId);
            given(confirmMeetingGetRequest.confirmDateTime()).willReturn(confirmDateTime);
            given(meetingService.getMemberIdInMeetingId(meetingId)).willReturn(List.of(memberId));
            doNothing().when(meeting).confirmDateTime(confirmDateTime);
            given(meeting.getConfirmDateTime()).willReturn(confirmDateTime);
            given(foodRepositoryService.findFoodById(foodId)).willReturn(null);
            doNothing().when(meetingValidationService).validateHostPermission(member, meeting);
            doNothing().when(meetingValidationService).validateMeetingConfirmation(meeting);

            // When & Then
            assertDoesNotThrow(() -> meetingEventService.confirmMeeting(memberId, meetingId,
                confirmMeetingGetRequest));
            verify(meeting).confirmDateTime(confirmDateTime);
            verify(meeting).confirmFood(any());
            verify(meetingEventCreateService).addMeetingEvent(eq(memberId),
                any(MeetingEventCreateRequest.class));
        }

        @Test
        void 실패_호스트_검증_실패한_경우() {
            // Given
            Long memberId = 1L;
            Long meetingId = 2L;

            Member member = mock(Member.class);
            Meeting meeting = mock(Meeting.class);
            ConfirmMeetingGetRequest confirmMeetingGetRequest = mock(
                ConfirmMeetingGetRequest.class);

            // Mocking
            given(memberService.findMember(memberId)).willReturn(member);
            given(meetingService.findMeeting(meetingId)).willReturn(meeting);
            doThrow(new MeetingHostUnauthorizedException("권한이 없습니다.")).when(
                meetingValidationService).validateHostPermission(member, meeting);

            // When
            Exception e = assertThrows(MeetingHostUnauthorizedException.class,
                () -> meetingEventService.confirmMeeting(memberId, meetingId,
                    confirmMeetingGetRequest));

            // Then
            assertEquals(e.getClass(), MeetingHostUnauthorizedException.class);
            assertEquals(e.getMessage(), "권한이 없습니다.");
            verify(meetingValidationService).validateHostPermission(member, meeting);
        }

        @Test
        void 실패_확정_모임_검증_실패한_경우() {
            // Given
            Long memberId = 1L;
            Long meetingId = 2L;

            Member member = mock(Member.class);
            Meeting meeting = mock(Meeting.class);
            ConfirmMeetingGetRequest confirmMeetingGetRequest = mock(
                ConfirmMeetingGetRequest.class);

            // Mocking
            given(memberService.findMember(memberId)).willReturn(member);
            given(meetingService.findMeeting(meetingId)).willReturn(meeting);
            doThrow(new MeetingAlreadyConfirmedException("이미 모임 시간이 확정되었습니다.")).when(
                meetingValidationService).validateMeetingConfirmation(meeting);

            // When
            Exception e = assertThrows(MeetingAlreadyConfirmedException.class,
                () -> meetingEventService.confirmMeeting(memberId, meetingId,
                    confirmMeetingGetRequest));

            // Then
            assertEquals(e.getClass(), MeetingAlreadyConfirmedException.class);
            assertEquals(e.getMessage(), "이미 모임 시간이 확정되었습니다.");
            verify(meetingValidationService).validateHostPermission(member, meeting);
            verify(meetingValidationService).validateMeetingConfirmation(meeting);
        }

        @Test
        void 실패_음식_찾을_수_없는_경우() {
            // Given
            Long memberId = 1L;
            Long meetingId = 2L;
            Long foodId = 3L;

            Member member = mock(Member.class);
            Meeting meeting = mock(Meeting.class);
            ConfirmMeetingGetRequest confirmMeetingGetRequest = mock(
                ConfirmMeetingGetRequest.class);

            // Mocking
            given(memberService.findMember(memberId)).willReturn(member);
            given(meetingService.findMeeting(meetingId)).willReturn(meeting);
            given(confirmMeetingGetRequest.confirmFoodId()).willReturn(foodId);
            given(foodRepositoryService.findFoodById(foodId)).willThrow(
                new FoodNotFoundException("음식을 찾을 수 없습니다."));

            // When
            Exception e = assertThrows(FoodNotFoundException.class,
                () -> meetingEventService.confirmMeeting(memberId, meetingId,
                    confirmMeetingGetRequest));

            // Then
            assertEquals(e.getClass(), FoodNotFoundException.class);
            assertEquals(e.getMessage(), "음식을 찾을 수 없습니다.");
            verify(meetingValidationService).validateHostPermission(member, meeting);
            verify(meetingValidationService).validateMeetingConfirmation(meeting);
            verify(foodRepositoryService).findFoodById(foodId);
        }
    }

    @Nested
    class 개인_일정_조회_테스트 {

        @Test
        void 성공_개인_모임_일정이_있는_경우() {
            // Given
            Long meetingId = 1L;
            Long memberId = 2L;

            Meeting meeting = mock(Meeting.class);
            Member member = mock(Member.class);
            MemberMeeting memberMeeting = mock(MemberMeeting.class);
            Time time = new Time(LocalDateTime.now(), LocalDateTime.now().plusHours(1),
                "Asia/Seoul", false);
            Event event = new Event(time);
            MeetingEvent meetingEvent = new MeetingEvent(memberMeeting, event);

            List<MeetingEvent> meetingEvents = List.of(meetingEvent);
            List<TimeGetResponse> expectedTimes = List.of(TimeGetResponse.from(time));
            MeetingPersonalEventGetResponse expectedResponse = new MeetingPersonalEventGetResponse(
                expectedTimes);

            // Mocking
            given(meetingService.findMeeting(meetingId)).willReturn(meeting);
            given(memberService.findMember(memberId)).willReturn(member);
            given(meetingService.findMemberMeeting(member, meeting)).willReturn(memberMeeting);
            given(meetingEventRepository.findByMemberMeeting(memberMeeting)).willReturn(
                meetingEvents);

            // When
            MeetingPersonalEventGetResponse actualResponse = meetingEventService.findMeetingPersonalEvent(
                meetingId, memberId);

            // Then
            assertEquals(expectedResponse, actualResponse);
            verify(meetingService).findMeeting(meetingId);
            verify(memberService).findMember(memberId);
            verify(meetingService).findMemberMeeting(member, meeting);
            verify(meetingEventRepository).findByMemberMeeting(memberMeeting);
        }

        @Test
        void 성공_개인_모임_일정이_빈_경우() {
            // Given
            Long meetingId = 1L;
            Long memberId = 2L;
            Meeting meeting = mock(Meeting.class);
            Member member = mock(Member.class);
            MemberMeeting memberMeeting = mock(MemberMeeting.class);

            // Mocking
            given(meetingService.findMeeting(meetingId)).willReturn(meeting);
            given(memberService.findMember(memberId)).willReturn(member);
            given(meetingService.findMemberMeeting(member, meeting)).willReturn(memberMeeting);
            given(meetingEventRepository.findByMemberMeeting(memberMeeting)).willReturn(List.of());

            // When
            MeetingPersonalEventGetResponse response = meetingEventService.findMeetingPersonalEvent(
                meetingId, memberId);

            // Then
            assertNotNull(response);
            assertTrue(response.meetingPersonalTimes().isEmpty());
        }

        @Test
        void 실패_모임_없는_경우() {
            // Given
            Long meetingId = 1L;
            Long memberId = 2L;

            // Mocking
            given(meetingService.findMeeting(meetingId)).willThrow(
                new MeetingNotFoundException("해당 모임이 존재하지 않습니다."));

            // When
            Exception e = assertThrows(MeetingNotFoundException.class,
                () -> meetingEventService.findMeetingPersonalEvent(meetingId, memberId));

            // Then
            assertEquals(e.getClass(), MeetingNotFoundException.class);
            assertEquals(e.getMessage(), "해당 모임이 존재하지 않습니다.");
            verify(meetingService).findMeeting(meetingId);
        }

        @Test
        void 실패_멤버_없는_경우() {
            // Given
            Long meetingId = 1L;
            Long memberId = 2L;
            Meeting meeting = mock(Meeting.class);

            // Mocking
            given(meetingService.findMeeting(meetingId)).willReturn(meeting);
            given(memberService.findMember(memberId)).willThrow(
                new MemberNotFoundException("해당 사용자가 존재하지 않습니다."));

            // When
            Exception e = assertThrows(MemberNotFoundException.class,
                () -> meetingEventService.findMeetingPersonalEvent(meetingId, memberId));

            // Then
            assertEquals(e.getClass(), MemberNotFoundException.class);
            assertEquals(e.getMessage(), "해당 사용자가 존재하지 않습니다.");
            verify(meetingService).findMeeting(meetingId);
            verify(memberService).findMember(memberId);
        }

        @Test
        void 실패_해당_모임에_멤버가_없는_경우() {
            // Given
            Long meetingId = 1L;
            Long memberId = 2L;
            Meeting meeting = mock(Meeting.class);
            Member member = mock(Member.class);

            // Mocking
            given(meetingService.findMeeting(meetingId)).willReturn(meeting);
            given(memberService.findMember(memberId)).willReturn(member);
            given(meetingService.findMemberMeeting(member, meeting)).willThrow(
                new MemberMeetingNotFoundException("해당 모임에 회원이 존재하지 않습니다."));

            // When
            Exception e = assertThrows(MemberMeetingNotFoundException.class,
                () -> meetingEventService.findMeetingPersonalEvent(meetingId, memberId));

            // Then
            assertEquals(e.getClass(), MemberMeetingNotFoundException.class);
            assertEquals(e.getMessage(), "해당 모임에 회원이 존재하지 않습니다.");
            verify(meetingService).findMeeting(meetingId);
            verify(memberService).findMember(memberId);
            verify(meetingService).findMemberMeeting(member, meeting);
        }
    }
}