package org.cookieandkakao.babting.domain.calendar.service;


import java.net.URI;
import java.util.Map;
import org.cookieandkakao.babting.common.properties.KakaoProviderProperties;
import org.cookieandkakao.babting.domain.calendar.dto.response.EventDetailGetResponse;
import org.cookieandkakao.babting.domain.calendar.dto.response.EventListGetResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Service
public class TalkCalendarClientService {

    private final RestClient restClient = RestClient.builder().build();
    private final KakaoProviderProperties kakaoProviderProperties;

    public TalkCalendarClientService(KakaoProviderProperties kakaoProviderProperties) {
        this.kakaoProviderProperties = kakaoProviderProperties;
    }

    public EventListGetResponse getEventList(String accessToken, String from, String to) {
        String url = kakaoProviderProperties.calendarEventListUri();
        URI uri = buildUri(url, from, to);
        try {
            ResponseEntity<EventListGetResponse> response = restClient.get()
                .uri(uri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .toEntity(EventListGetResponse.class);
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("API 호출 중 오류 발생");
        }
    }

    public EventDetailGetResponse getEvent(String accessToken, String eventId) {
        String url = kakaoProviderProperties.calendarEventDetailUri();
        URI uri = buildGetEventUri(url, eventId);

        try {
            ResponseEntity<EventDetailGetResponse> response = restClient.get()
                .uri(uri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .toEntity(EventDetailGetResponse.class);
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("API 호출 중 오류 발생", e);
        }
    }

    public Map<String, Object> createEvent(String accessToken,
        MultiValueMap<String, String> formData) {
        String url = kakaoProviderProperties.calendarCreateEventUri();
        URI uri = URI.create(url);
        try {
            ResponseEntity<Map> response = restClient.post()
                .uri(uri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .header(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded")
                .body(formData)
                .retrieve()
                .toEntity(Map.class);
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("API 호출 중 오류 발생", e);
        }
    }

    private URI buildUri(String baseUrl, String from, String to) {
        return URI.create(
            String.format("%s?from=%s&to=%s&limit=100&time_zone=Asia/Seoul", baseUrl, from, to));
    }

    private URI buildGetEventUri(String baseUrl, String eventId) {
        return URI.create(String.format("%s?event_id=%s", baseUrl, eventId));
    }

}
