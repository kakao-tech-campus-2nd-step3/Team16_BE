package org.cookieandkakao.babting.domain.meeting.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;
import org.cookieandkakao.babting.common.exception.customexception.ApiException;
import org.cookieandkakao.babting.domain.calendar.dto.response.TimeGetResponse;
import org.cookieandkakao.babting.domain.meeting.dto.request.ConfirmMeetingGetRequest;
import org.cookieandkakao.babting.domain.meeting.dto.request.MeetingJoinCreateRequest;
import org.cookieandkakao.babting.domain.meeting.dto.request.MeetingTimeCreateRequest;
import org.cookieandkakao.babting.domain.meeting.dto.response.MeetingPersonalEventGetResponse;
import org.cookieandkakao.babting.domain.meeting.service.MeetingEventService;
import org.cookieandkakao.babting.domain.meeting.service.MeetingJoinService;
import org.cookieandkakao.babting.domain.meeting.service.MeetingService;
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

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class MeetingControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MeetingService meetingService;

    @MockBean
    private MeetingEventService meetingEventService;

    @MockBean
    private MeetingJoinService meetingJoinService;

    private static final Long MEMBER_ID = 1L;
    private static final Long MEETING_ID = 2L;
    private String accessToken;

    @BeforeEach
    void setUp() {
        TokenIssueResponse tokenIssueResponse = jwtUtil.issueToken(MEMBER_ID);
        accessToken = tokenIssueResponse.accessToken();
    }

    @Nested
    class 모임_참가_통합_테스트 {

        @Test
        void 성공() throws Exception {
            // Given
            List<Long> preferences = List.of(1L, 2L);
            List<Long> nonPreferences = List.of(3L, 4L);
            MeetingTimeCreateRequest meetingTimeCreateRequest = new MeetingTimeCreateRequest(
                "2024-01-01T10:00:00Z", "2024-01-01T12:00:00Z", "Asia/Seoul", false);
            List<MeetingTimeCreateRequest> meetingTimeCreateRequests = List.of(
                meetingTimeCreateRequest);
            MeetingJoinCreateRequest meetingJoinCreateRequest = new MeetingJoinCreateRequest(
                preferences, nonPreferences, meetingTimeCreateRequests);
            String requestJson = objectMapper.writeValueAsString(meetingJoinCreateRequest);

            // When & Then
            mockMvc.perform(post("/api/meeting/{meetingId}/join", MEETING_ID)
                    .header("Authorization", accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("모임 참가 성공"));
        }

        @Test
        void 실패_유효하지_않은_토큰() throws Exception {
            // Given
            String invalidToken = "InvalidToken";

            // When & Then
            mockMvc.perform(post("/api/meeting/{meetingId}/join", MEETING_ID)
                    .header("Authorization", invalidToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("유효하지 않은 토큰입니다."));
        }
    }

    @Nested
    class 모임_확정_통합_테스트 {

        @Test
        void 성공() throws Exception {
            // Given
            ConfirmMeetingGetRequest confirmMeetingGetRequest = new ConfirmMeetingGetRequest(
                LocalDateTime.of(2024, 1, 1, 10, 0), 1L);

            String requestJson = objectMapper.writeValueAsString(confirmMeetingGetRequest);

            // When & Then
            mockMvc.perform(post("/api/meeting/{meetingId}/confirm", MEETING_ID)
                    .header("Authorization", accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.message").value("모임 확정 성공"));
        }

        @Test
        void 실패_유효하지_않은_데이터() throws Exception {
            // Given
            String invalidRequestJson = "{}";

            // When & Then
            mockMvc.perform(post("/api/meeting/{meetingId}/confirm", MEETING_ID)
                    .header("Authorization", accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(invalidRequestJson))
                .andExpect(status().isBadRequest());
        }

        @Test
        void 실패_유효하지_않은_토큰() throws Exception {
            // Given
            String invalidToken = "InvalidToken";
            ConfirmMeetingGetRequest confirmMeetingGetRequest = new ConfirmMeetingGetRequest(
                LocalDateTime.of(2024, 1, 1, 10, 0), 1L);
            String requestJson = objectMapper.writeValueAsString(confirmMeetingGetRequest);

            // When & Then
            mockMvc.perform(post("/api/meeting/{meetingId}/confirm", MEETING_ID)
                    .header("Authorization", invalidToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("유효하지 않은 토큰입니다."));
        }
    }

    @Nested
    class 모임별_개인_일정_조회_통합_테스트 {

        @Test
        void 성공() throws Exception {
            // Given
            TimeGetResponse timeGetResponse = new TimeGetResponse(
                "2024-01-01T10:00:00", "2024-01-01T12:00:00", "Asia/Seoul", false);
            MeetingPersonalEventGetResponse response = new MeetingPersonalEventGetResponse(
                List.of(timeGetResponse));

            // Mocking
            given(meetingEventService.findMeetingPersonalEvent(MEETING_ID, MEMBER_ID)).willReturn(
                response);

            // When & Then
            mockMvc.perform(get("/api/meeting/{meetingId}/personal-event", MEETING_ID)
                    .header("Authorization", accessToken)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.meetingPersonalTimes[0].start_at").value(
                    "2024-01-01T10:00:00"))
                .andExpect(
                    jsonPath("$.data.meetingPersonalTimes[0].end_at").value("2024-01-01T12:00:00"))
                .andExpect(jsonPath("$.message").value("모임별 개인 일정 조회 성공"));

        }

        @Test
        void 실패_유효하지_않은_토큰() throws Exception {
            // Given
            String invalidToken = "InvalidToken";

            // When & Then
            mockMvc.perform(get("/api/meeting/{meetingId}/personal-event", MEETING_ID)
                    .header("Authorization", invalidToken)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("유효하지 않은 토큰입니다."));
        }

        @Test
        void 실패_일정_조회_오류() throws Exception {
            // Mocking
            given(meetingEventService.findMeetingPersonalEvent(MEETING_ID, MEMBER_ID))
                .willThrow(new ApiException("개인 일정 조회 중 오류가 발생했습니다."));

            // When & Then
            mockMvc.perform(get("/api/meeting/{meetingId}/personal-event", MEETING_ID)
                    .header("Authorization", accessToken)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("개인 일정 조회 중 오류가 발생했습니다."));
        }
    }
}
