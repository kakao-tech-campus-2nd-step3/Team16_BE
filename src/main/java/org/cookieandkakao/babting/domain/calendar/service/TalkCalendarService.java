package org.cookieandkakao.babting.domain.calendar.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.util.Map;
import org.cookieandkakao.babting.domain.calendar.dto.request.EventCreateRequestDto;
import org.cookieandkakao.babting.domain.calendar.dto.response.EventCreateResponseDto;
import org.cookieandkakao.babting.domain.calendar.dto.response.EventListGetResponseDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Service
public class TalkCalendarService {

    private final RestClient restClient = RestClient.builder().build();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final EventService eventService;

    public TalkCalendarService(EventService eventService) {
        this.eventService = eventService;
    }

    public EventListGetResponseDto getEventList(String accessToken, String from, String to) {
        String url = "https://kapi.kakao.com/v2/api/calendar/events";
        URI uri = buildUri(url, from, to);
        try {
            ResponseEntity<EventListGetResponseDto> response = restClient.get()
                .uri(uri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .toEntity(EventListGetResponseDto.class);
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("API 호출 중 오류 발생");
        }
    }

    private URI buildUri(String baseUrl, String from, String to) {
        return URI.create(
            String.format("%s?from=%s&to=%s&limit=100&time_zone=Asia/Seoul", baseUrl, from, to));
    }

    public EventCreateResponseDto createEvent(String accessToken,
        EventCreateRequestDto eventCreateRequestDto, Long memberId) {
        String url = "https://kapi.kakao.com/v2/api/calendar/create/event";
        URI uri = URI.create(url);

        try {
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();

            // event라는 key에 JSON 형태의 데이터를 추가해야 함
            // EventCreateRequestDto를 JSON으로 변환
            String eventJson = convertToJSONString(eventCreateRequestDto);

            // event라는 key로 JSON 데이터를 추가
            formData.add("event", eventJson);

            // POST 요청 실행
            ResponseEntity<Map> response = restClient.post()
                .uri(uri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .header(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded")
                .body(formData)
                .retrieve()
                .toEntity(Map.class);

            // 응답에서 event_id 추출
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null && responseBody.containsKey("event_id")) {
                String eventId = responseBody.get("event_id").toString();
                // EventService를 호출하여 일정 저장
                eventService.saveCreatedEvent(eventCreateRequestDto, eventId, memberId);
                // EventCreateResponseDto로 응답 반환
                return new EventCreateResponseDto(eventId);
            }
            throw new RuntimeException("Event 생성 중 오류 발생: 응답에서 event_id가 없습니다.");

        } catch (Exception e) {
            throw new RuntimeException("API 호출 중 오류 발생");
        }
    }

    // EventCreateRequestDto를 JSON 문자열로 변환하는 메서드
    private String convertToJSONString(EventCreateRequestDto eventCreateRequestDto) {
        try {
            return objectMapper.writeValueAsString(eventCreateRequestDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
