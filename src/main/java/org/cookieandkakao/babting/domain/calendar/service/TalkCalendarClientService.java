package org.cookieandkakao.babting.domain.calendar.service;

import org.cookieandkakao.babting.common.exception.customexception.ApiException;
import org.cookieandkakao.babting.common.properties.KakaoProviderProperties;
import org.cookieandkakao.babting.domain.calendar.dto.response.EventCreateResponse;
import org.cookieandkakao.babting.domain.calendar.dto.response.EventDetailGetResponse;
import org.cookieandkakao.babting.domain.calendar.dto.response.EventListGetResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
public class TalkCalendarClientService {

    private final RestClient restClient;
    private final KakaoProviderProperties kakaoProviderProperties;

    public TalkCalendarClientService(RestClient kakaoRestClient,
        KakaoProviderProperties kakaoProviderProperties) {
        this.restClient = kakaoRestClient;
        this.kakaoProviderProperties = kakaoProviderProperties;
    }

    public EventListGetResponse getEventList(String accessToken, String from, String to) {
        String relativeUrl = kakaoProviderProperties.calendarEventListUri();
        try {
            ResponseEntity<EventListGetResponse> response = restClient.get()
                .uri(uriBuilder -> uriBuilder.path(relativeUrl)
                    .queryParam("from", from)
                    .queryParam("to", to)
                    .queryParam("limit", 100)
                    .queryParam("time_zone", "Asia/Seoul")
                    .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .toEntity(EventListGetResponse.class);
            return response.getBody();
        } catch (RestClientException e) {
            throw new ApiException("일정 목록 조회 중 에러 발생 : " + e.getMessage());
        }
    }

    public EventDetailGetResponse getEvent(String accessToken, String eventId) {
        String relativeUrl = kakaoProviderProperties.calendarEventDetailUri();
        try {
            ResponseEntity<EventDetailGetResponse> response = restClient.get()
                .uri(uriBuilder -> uriBuilder.path(relativeUrl)
                    .queryParam("event_id", eventId)
                    .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .toEntity(EventDetailGetResponse.class);
            return response.getBody();
        } catch (RestClientException e) {
            throw new ApiException("일정 상세 조회 중 오류 발생 : " + e.getMessage());
        }
    }

    public EventCreateResponse createEvent(String accessToken,
        MultiValueMap<String, String> formData) {
        String relativeUrl = kakaoProviderProperties.calendarCreateEventUri();
        try {
            ResponseEntity<EventCreateResponse> response = restClient.post()
                .uri(relativeUrl)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .header(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded")
                .body(formData)
                .retrieve()
                .toEntity(EventCreateResponse.class);
            return response.getBody();
        } catch (RestClientException e) {
            throw new ApiException("일정 생성 중 오류 발생 : " + e.getMessage());
        }
    }
}
