package org.cookieandkakao.babting.domain.calendar.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.cookieandkakao.babting.common.apiresponse.ApiResponseBody.SuccessBody;
import org.cookieandkakao.babting.common.apiresponse.ApiResponseGenerator;
import org.cookieandkakao.babting.domain.calendar.dto.request.EventCreateRequest;
import org.cookieandkakao.babting.domain.calendar.dto.response.EventCreateResponse;
import org.cookieandkakao.babting.domain.calendar.dto.response.EventDetailGetResponse;
import org.cookieandkakao.babting.domain.calendar.dto.response.EventGetResponse;
import org.cookieandkakao.babting.domain.calendar.dto.response.EventListGetResponse;
import org.cookieandkakao.babting.domain.calendar.service.EventService;
import org.cookieandkakao.babting.domain.calendar.service.TalkCalendarService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/calendar")
public class TalkCalendarController {

    private final TalkCalendarService talkCalendarService;
    private final EventService eventService;

    public TalkCalendarController(TalkCalendarService talkCalendarService,
        EventService eventService) {
        this.talkCalendarService = talkCalendarService;
        this.eventService = eventService;
    }

    @GetMapping("/events")
    public ResponseEntity<SuccessBody<EventListGetResponse>> getEventList(
        @RequestHeader(value = "Authorization") String authorizationHeader,
        @RequestParam String from,
        @RequestParam String to,
        @RequestParam Long memberId
    ) {
        String accessToken = authorizationHeader.replace("Bearer ", "");

        List<EventGetResponse> updatedEvents = talkCalendarService.getUpdatedEventList(accessToken, from, to, memberId);
        EventListGetResponse eventList = new EventListGetResponse(updatedEvents);

        if (updatedEvents.isEmpty()) {
            return ApiResponseGenerator.success(HttpStatus.NO_CONTENT, "조회된 일정이 없습니다.", eventList);
        }

        return ApiResponseGenerator.success(HttpStatus.OK, "일정 목록을 조회했습니다.", eventList);
    }

    @GetMapping("/events/{event_id}")
    public ResponseEntity<SuccessBody<EventDetailGetResponse>> getEvent(
        @RequestHeader(value = "Authorization") String authorizationHeader,
        @PathVariable("event_id") String eventId,
        @RequestParam Long memberId
    ) {
        String accessToken = authorizationHeader.replace("Bearer ", "");

        EventDetailGetResponse eventDetailGetResponse = talkCalendarService.getEvent(accessToken, eventId);

        if (eventDetailGetResponse == null) {
            return ApiResponseGenerator.success(HttpStatus.NO_CONTENT, "조회된 일정이 없습니다.",
                eventDetailGetResponse);
        }

        return ApiResponseGenerator.success(HttpStatus.OK, "일정 목록을 조회했습니다.", eventDetailGetResponse);
    }

    @PostMapping("/events")
    public ResponseEntity<SuccessBody<EventCreateResponse>> createEvent(
        @RequestHeader(value = "Authorization") String authorizationHeader,
        @Valid @RequestBody EventCreateRequest eventRequestDto,
        @RequestParam Long memberId
    ) {
        String accessToken = authorizationHeader.replace("Bearer ", "");
        // 카카오 api로 일정 생성
        EventCreateResponse eventCreateResponse = talkCalendarService.createEvent(
            accessToken, eventRequestDto, memberId);
        return ApiResponseGenerator.success(HttpStatus.CREATED, "일정이 성공적으로 생성되었습니다.",
            eventCreateResponse);
    }
}
