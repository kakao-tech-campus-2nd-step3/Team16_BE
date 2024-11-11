package org.cookieandkakao.babting.domain.calendar.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.cookieandkakao.babting.common.cache.CacheKeyGenerator;
import org.cookieandkakao.babting.common.exception.customexception.CacheEvictionException;
import org.cookieandkakao.babting.domain.calendar.exception.EventCreationException;
import org.cookieandkakao.babting.domain.calendar.exception.JsonConversionException;
import org.cookieandkakao.babting.domain.calendar.dto.request.EventCreateRequest;
import org.cookieandkakao.babting.domain.calendar.dto.response.EventCreateResponse;
import org.cookieandkakao.babting.domain.calendar.dto.response.EventDetailGetResponse;
import org.cookieandkakao.babting.domain.calendar.dto.response.EventGetResponse;
import org.cookieandkakao.babting.domain.calendar.dto.response.EventListGetResponse;
import org.cookieandkakao.babting.domain.member.service.MemberService;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Service
public class TalkCalendarService {

    private final TalkCalendarClientService talkCalendarClientService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final MemberService memberService;
    private final RedisTemplate<String, String> redisTemplate;
    private static final Duration CACHE_DURATION = Duration.ofMinutes(5);

    public TalkCalendarService(
        TalkCalendarClientService talkCalendarClientService,
        MemberService memberService,
        RedisTemplate<String, String> redisTemplate) {
        this.talkCalendarClientService = talkCalendarClientService;
        this.memberService = memberService;
        this.redisTemplate = redisTemplate;
    }

    public List<EventGetResponse> getUpdatedEventList(String from, String to, Long memberId) {
        String cacheKey = CacheKeyGenerator.generateEventListKey(memberId, from, to);
        String cachedJson = redisTemplate.opsForValue().get(cacheKey);
        if (cachedJson != null) {
            try {
                // JSON 문자열을 List<EventGetResponse> 로 변환
                // 리스트이므로 TypeReference 사용
                return objectMapper.readValue(cachedJson,
                    new TypeReference<List<EventGetResponse>>() {
                    });
            } catch (JsonProcessingException e) {
                throw new JsonConversionException("캐시된 JSON 변환 오류");
            }
        }

        String kakaoAccessToken = memberService.getKakaoAccessToken(memberId);
        EventListGetResponse eventList = talkCalendarClientService.getEventList(kakaoAccessToken,
            from, to);
        List<EventGetResponse> updatedEvents = new ArrayList<>();

        for (EventGetResponse event : eventList.events()) {
            if (event.id() != null) {
                event = talkCalendarClientService.getEvent(kakaoAccessToken, event.id()).event();
                updatedEvents.add(event);
            } else {
                updatedEvents.add(event);
            }
        }
        cacheData(cacheKey, updatedEvents);

        return updatedEvents;
    }

    public EventDetailGetResponse getEvent(Long memberId, String eventId) {
        String cacheKey = CacheKeyGenerator.generateEventDetailKey(eventId);
        String cachedJson = redisTemplate.opsForValue().get(cacheKey);

        if (cachedJson != null) {
            try {
                // JSON 문자열을 EventDetailGetResponse 로 변환
                // 단일 객체이므로 .class 사용
                return objectMapper.readValue(cachedJson, EventDetailGetResponse.class);
            } catch (JsonProcessingException e) {
                throw new JsonConversionException("캐시된 JSON 변환 오류");
            }
        }

        String kakaoAccessToken = memberService.getKakaoAccessToken(memberId);
        EventDetailGetResponse eventDetailGetResponse = talkCalendarClientService.getEvent(
            kakaoAccessToken, eventId);

        cacheData(cacheKey, eventDetailGetResponse);
        return eventDetailGetResponse;
    }

    // JSON 변환 후 Redis에 저장
    private void cacheData(String cacheKey, Object data) {
        try {
            redisTemplate.opsForValue()
                .set(cacheKey, objectMapper.writeValueAsString(data), CACHE_DURATION);
        } catch (JsonProcessingException e) {
            throw new JsonConversionException("JSON 변환 오류");
        }
    }

    public EventCreateResponse createEvent(
        EventCreateRequest eventCreateRequest, Long memberId) {
        String kakaoAccessToken = memberService.getKakaoAccessToken(memberId);
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        // event라는 key에 JSON 형태의 데이터를 추가해야 함
        // EventCreateRequestDto를 JSON으로 변환
        String eventJson = convertToJSONString(eventCreateRequest);
        // event라는 key로 JSON 데이터를 추가
        formData.add("event", eventJson);
        EventCreateResponse responseBody = Optional.ofNullable(
            talkCalendarClientService.createEvent(kakaoAccessToken, formData)).orElseThrow(
            () -> new EventCreationException("Event 생성 중 오류 발생: 응답에서 event_id가 없습니다."));
        evictMemberCache(memberId);
        return responseBody;
    }

    // EventCreateRequestDto를 JSON 문자열로 변환하는 메서드
    private String convertToJSONString(EventCreateRequest eventCreateRequest) {
        try {
            return objectMapper.writeValueAsString(eventCreateRequest);
        } catch (JsonProcessingException e) {
            throw new JsonConversionException("JSON 변환 중 오류가 발생했습니다.");
        }
    }

    // 키 값에서 memberId가 포함되어 있는 것 삭제
    private void evictMemberCache(Long memberId) {
        String pattern = CacheKeyGenerator.generateEventListPattern(memberId);
        // 패턴과 카운트 설정
        ScanOptions scanOptions = ScanOptions.scanOptions().match(pattern).count(10).build();
        // 스캔 시작
        try {
            Cursor<byte[]> cursor = redisTemplate.executeWithStickyConnection(
                connection -> connection.scan(scanOptions));

            List<String> keysToDelete = new ArrayList<>();
            while (cursor.hasNext()) {
                keysToDelete.add(new String(cursor.next()));
            }

            if (!keysToDelete.isEmpty()) {
                redisTemplate.delete(keysToDelete);
            }

            // 리소스 해제
            // Cursor 사용 후 반드시 닫아주어야 함
            cursor.close();
        } catch (DataAccessException e) {
            throw new CacheEvictionException("Redis 데이터 접근 중 문제가 발생했습니다.");
        } catch (Exception e) {
            throw new CacheEvictionException("Cursor 리소스를 해제하는 중 문제가 발생했습니다.");
        }
    }
}
