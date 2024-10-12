package org.cookieandkakao.babting.domain.calendar.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.cookieandkakao.babting.common.exception.customexception.EventCreationException;
import org.cookieandkakao.babting.common.exception.customexception.JsonConversionException;
import org.cookieandkakao.babting.domain.calendar.dto.request.EventCreateRequest;
import org.cookieandkakao.babting.domain.calendar.dto.response.EventCreateResponse;
import org.cookieandkakao.babting.domain.calendar.dto.response.EventDetailGetResponse;
import org.cookieandkakao.babting.domain.calendar.dto.response.EventGetResponse;
import org.cookieandkakao.babting.domain.calendar.dto.response.EventListGetResponse;
import org.cookieandkakao.babting.domain.member.entity.KakaoToken;
import org.cookieandkakao.babting.domain.member.service.MemberService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Service
public class TalkCalendarService {

    private final TalkCalendarClientService talkCalendarClientService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final EventService eventService;
    private final MemberService memberService;

    public TalkCalendarService(EventService eventService, TalkCalendarClientService talkCalendarClientService,
        MemberService memberService) {
        this.eventService = eventService;
        this.talkCalendarClientService = talkCalendarClientService;
        this.memberService = memberService;
    }

    // 일정 목록을 조회할 때 캐시 적용
    @Cacheable(value = "eventListCache", key = "#memberId")
    public List<EventGetResponse> getUpdatedEventList(String from, String to, Long memberId) {
        String kakaoAccessToken = getKakaoAccessToken(memberId);
        EventListGetResponse eventList = talkCalendarClientService.getEventList(kakaoAccessToken, from, to);
        List<EventGetResponse> updatedEvents = new ArrayList<>();

        for (EventGetResponse event : eventList.events()) {
            if (event.id() != null) {
                event = talkCalendarClientService.getEvent(kakaoAccessToken, event.id()).event();
                updatedEvents.add(event);
            } else {
                updatedEvents.add(event);
            }
        }

        return updatedEvents;
    }

    @Cacheable(value = "eventDetailCache", key = "#eventId")
    public EventDetailGetResponse getEvent(Long memberId, String eventId) {
        String kakaoAccessToken = getKakaoAccessToken(memberId);
        return talkCalendarClientService.getEvent(kakaoAccessToken, eventId);
    }

    @CacheEvict(value = "eventListCache", key = "#memberId")
    public EventCreateResponse createEvent(
        EventCreateRequest eventCreateRequest, Long memberId) {
        String kakaoAccessToken = getKakaoAccessToken(memberId);
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        // event라는 key에 JSON 형태의 데이터를 추가해야 함
        // EventCreateRequestDto를 JSON으로 변환
        String eventJson = convertToJSONString(eventCreateRequest);
        // event라는 key로 JSON 데이터를 추가
        formData.add("event", eventJson);
        // 응답에서 event_id 추출
        Map<String, Object> responseBody =talkCalendarClientService.createEvent(kakaoAccessToken, formData);
        if (responseBody != null && responseBody.containsKey("event_id")) {
            String eventId = responseBody.get("event_id").toString();
            // EventCreateResponseDto로 응답 반환
            return new EventCreateResponse(eventId);
        }
        throw new EventCreationException("Event 생성 중 오류 발생: 응답에서 event_id가 없습니다.");
    }

    // EventCreateRequestDto를 JSON 문자열로 변환하는 메서드
    private String convertToJSONString(EventCreateRequest eventCreateRequest) {
        try {
            return objectMapper.writeValueAsString(eventCreateRequest);
        } catch (JsonProcessingException e) {
            throw new JsonConversionException("JSON 변환 중 오류가 발생했습니다.");
        }
    }
    private String getKakaoAccessToken(Long memberId) {
        KakaoToken kakaoToken = memberService.getKakaoToken(memberId);
        String kakaoAccessToken = kakaoToken.getAccessToken();
        return kakaoAccessToken;
    }
}
