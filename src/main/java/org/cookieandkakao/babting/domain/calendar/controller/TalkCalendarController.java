package org.cookieandkakao.babting.domain.calendar.controller;

import org.cookieandkakao.babting.common.apiresponse.ApiResponseBody.SuccessBody;
import org.cookieandkakao.babting.common.apiresponse.ApiResponseGenerator;
import org.cookieandkakao.babting.domain.calendar.dto.request.EventCreateRequestDto;
import org.cookieandkakao.babting.domain.calendar.dto.response.EventCreateResponseDto;
import org.cookieandkakao.babting.domain.calendar.dto.response.EventGetResponseDto;
import org.cookieandkakao.babting.domain.calendar.dto.response.EventListGetResponseDto;
import org.cookieandkakao.babting.domain.calendar.service.EventService;
import org.cookieandkakao.babting.domain.calendar.service.TalkCalendarService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
    public ResponseEntity<SuccessBody<EventListGetResponseDto>> getEventList(
        @RequestHeader(value = "Authorization") String authorizationHeader,
        @RequestParam String from,
        @RequestParam String to,
        @RequestParam Long memberId
    ) {
        String accessToken = authorizationHeader.replace("Bearer ", "");

        EventListGetResponseDto eventList = talkCalendarService.getEventList(accessToken, from, to);

        for (EventGetResponseDto event : eventList.events()) {
            eventService.saveEvent(event, memberId);
        }

        if (eventList.events().isEmpty()) {
            return ApiResponseGenerator.success(HttpStatus.NO_CONTENT, "조회된 일정이 없습니다.",
                eventList);
        }

        return ApiResponseGenerator.success(HttpStatus.OK, "일정 목록을 조회했습니다.",
            eventList);
    }

    @PostMapping("/events")
    public ResponseEntity<SuccessBody<EventCreateResponseDto>> createEvent(
        @RequestHeader(value = "Authorization") String authorizationHeader,
        @RequestBody EventCreateRequestDto eventRequestDto,
        @RequestParam Long memberId
    ) {
        String accessToken = authorizationHeader.replace("Bearer ", "");
        // 카카오 api로 일정 생성
        EventCreateResponseDto eventCreateResponseDto = talkCalendarService.createEvent(
            accessToken, eventRequestDto, memberId);
        return ApiResponseGenerator.success(HttpStatus.OK, "일정이 성공적으로 생성되었습니다.",
            eventCreateResponseDto);
    }
}
