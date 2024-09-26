package org.cookieandkakao.babting.domain.calendar.service;

import java.net.URI;
import org.cookieandkakao.babting.domain.calendar.dto.EventListDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class TalkCalendarService {

    private final RestClient restClient = RestClient.builder().build();

    public EventListDTO getEventList(String accessToken, String from, String to) {
        String url = "https://kapi.kakao.com/v2/api/calendar/events";
        URI uri = buildUri(url, from, to);
        try {
            ResponseEntity<EventListDTO> response = restClient.get()
                .uri(uri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .toEntity(EventListDTO.class);
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("API 호출 중 오류 발생: " + e.getMessage(), e);
        }
    }

    private URI buildUri(String baseUrl, String from, String to) {
        return URI.create(String.format("%s?from=%s&to=%s&limit=100&time_zone=Asia/Seoul", baseUrl, from, to));
    }
}
