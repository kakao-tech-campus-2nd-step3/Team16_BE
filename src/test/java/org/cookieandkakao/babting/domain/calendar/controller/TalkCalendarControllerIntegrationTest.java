package org.cookieandkakao.babting.domain.calendar.controller;

import java.util.List;
import org.cookieandkakao.babting.common.exception.customexception.ApiException;
import org.cookieandkakao.babting.domain.calendar.dto.response.EventDetailGetResponse;
import org.cookieandkakao.babting.domain.calendar.dto.response.EventGetResponse;
import org.cookieandkakao.babting.domain.calendar.dto.response.EventListGetResponse;
import org.cookieandkakao.babting.domain.calendar.exception.EventDetailNotFoundException;
import org.cookieandkakao.babting.domain.calendar.service.TalkCalendarService;
import org.cookieandkakao.babting.domain.member.dto.TokenIssueResponse;
import org.cookieandkakao.babting.domain.member.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class TalkCalendarControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @MockBean
    private TalkCalendarService talkCalendarService;

    private static final Long MEMBER_ID = 1L;
    private String accessToken;

    @BeforeEach
    void setUp() {
        TokenIssueResponse tokenIssueResponse = jwtUtil.issueToken(MEMBER_ID);
        accessToken = tokenIssueResponse.accessToken();
    }


    @Nested
    class 일정_목록_조회_통합_테스트 {

        @Test
        void 성공_일정_목록_있는_경우() throws Exception {
            // Given
            String from = "2024-01-01T00:00:00Z";
            String to = "2024-01-31T00:00:00Z";

            EventGetResponse event = new EventGetResponse(
                "testId", "Test Event", "USER", null, false, false, null, null,
                "Test Description", null, null, "TestColor", null);

            List<EventGetResponse> eventList = List.of(event);
            EventListGetResponse eventListGetResponse = new EventListGetResponse(eventList);

            // Mocking
            given(talkCalendarService.getUpdatedEventList(from, to, MEMBER_ID)).willReturn(
                eventList);

            // When & Then
            mockMvc.perform(get("/api/calendar/events")
                    .param("from", from)
                    .param("to", to)
                    .header("Authorization", accessToken)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.events[0].id").value("testId"))
                .andExpect(jsonPath("$.data.events[0].title").value("Test Event"))
                .andExpect(jsonPath("$.message").value("일정 목록을 조회했습니다."));
        }

        @Test
        void 성공_일정_목록_없는_경우() throws Exception {
            // Given
            String from = "2024-01-01T00:00:00Z";
            String to = "2024-01-31T00:00:00Z";

            List<EventGetResponse> eventList = List.of();
            EventListGetResponse eventListGetResponse = new EventListGetResponse(eventList);

            // Mocking
            given(talkCalendarService.getUpdatedEventList(from, to, MEMBER_ID)).willReturn(
                eventList);

            // When & Then
            mockMvc.perform(get("/api/calendar/events")
                    .param("from", from)
                    .param("to", to)
                    .header("Authorization", accessToken)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("조회된 일정 목록이 없습니다."))
                .andExpect(jsonPath("$.data.events").isEmpty());
        }

        @Test
        void 실패_ApiException예외() throws Exception {
            // Given
            String from = "2024-01-01T00:00:00Z";
            String to = "2024-01-31T00:00:00Z";

            // Mocking
            given(talkCalendarService.getUpdatedEventList(from, to, MEMBER_ID)).willThrow(
                new ApiException("API 호출 중 오류 발생"));

            // When & Then
            mockMvc.perform(get("/api/calendar/events")
                    .param("from", from)
                    .param("to", to)
                    .header("Authorization", accessToken))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("API 호출 중 오류 발생"));
        }

        @Test
        void 실패_유효하지_않은_토큰() throws Exception {
            // Given
            String from = "2024-01-01T00:00:00Z";
            String to = "2024-01-31T00:00:00Z";
            String invalidToken = "InvalidToken";

            // When & Then
            mockMvc.perform(get("/api/calendar/events")
                    .param("from", from)
                    .param("to", to)
                    .header("Authorization", invalidToken))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("유효하지 않은 토큰입니다."));
        }

        @Test
        void 실패_유효하지_않은_파라미터() throws Exception {
            // Given
            String invalidFrom = "InvalidFrom";
            String invalidTo = "InvalidTo";

            // When &Then
            mockMvc.perform(get("/api/calendar/events")
                    .param("from", invalidFrom)
                    .param("to", invalidTo)
                    .header("Authorization", accessToken))
                .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class 일정_상세_조회_통합_테스트 {

        @Test
        void 성공_일정_있는_경우() throws Exception {
            // Given
            String eventId = "testId";
            EventGetResponse event = new EventGetResponse(
                "testId", "Test Event", "USER", null, false, false, null, null,
                "Test Description", null, null, "TestColor", null);
            EventDetailGetResponse eventDetailGetResponse = new EventDetailGetResponse(event);

            // Mocking
            given(talkCalendarService.getEvent(MEMBER_ID, eventId)).willReturn(
                eventDetailGetResponse);

            // When & Then
            mockMvc.perform(get("/api/calendar/events/{event_id}", eventId)
                    .header("Authorization", accessToken)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.event.id").value("testId"))
                .andExpect(jsonPath("$.data.event.title").value("Test Event"))
                .andExpect(jsonPath("$.message").value("일정을 조회했습니다."));
        }

        @Test
        void 실패_일정_없는_경우() throws Exception {
            // Given
            String eventId = "testId";

            // Mocking
            given(talkCalendarService.getEvent(MEMBER_ID, eventId)).willThrow(
                new EventDetailNotFoundException("요청한 일정 ID에 해당하는 일정을 찾을 수 없습니다."));

            // When & Then
            mockMvc.perform(get("/api/calendar/events/{event_id}", eventId)
                    .header("Authorization", accessToken)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("요청한 일정 ID에 해당하는 일정을 찾을 수 없습니다."));
        }

        @Test
        void 실패_ApiException예외() throws Exception {
            // Given
            String eventId = "testId";

            // Mocking
            given(talkCalendarService.getEvent(MEMBER_ID, eventId)).willThrow(
                new ApiException("API 호출 중 오류 발생"));

            // When & Then
            mockMvc.perform(get("/api/calendar/events/{event_id}", eventId)
                    .header("Authorization", accessToken)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("API 호출 중 오류 발생"));
        }

        @Test
        void 실패_유효하지_않은_토큰() throws Exception {
            // Given
            String eventId = "testId";
            String invalidToken = "InvalidToken";

            // When & Then
            mockMvc.perform(get("/api/calendar/events/{event_id}", eventId)
                    .header("Authorization", invalidToken)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("유효하지 않은 토큰입니다."));
        }
    }

}
