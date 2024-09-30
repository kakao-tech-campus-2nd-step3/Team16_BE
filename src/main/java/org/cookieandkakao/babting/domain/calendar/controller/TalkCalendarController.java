package org.cookieandkakao.babting.domain.calendar.controller;

import org.cookieandkakao.babting.common.apiresponse.ApiResponseGenerator;
import org.cookieandkakao.babting.domain.calendar.dto.request.EventCreateRequestDto;
import org.cookieandkakao.babting.domain.calendar.dto.response.EventCreateResponseDto;
import org.cookieandkakao.babting.domain.calendar.dto.response.EventGetResponseDto;
import org.cookieandkakao.babting.domain.calendar.dto.response.EventListGetResponseDto;
import org.cookieandkakao.babting.domain.calendar.dto.request.EventListGetRequestDto;
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

    public TalkCalendarController(TalkCalendarService talkCalendarService, EventService eventService) {
        this.talkCalendarService = talkCalendarService;
        this.eventService = eventService;
    }

    @GetMapping("/events")
    public ResponseEntity<?> getEventList(
        @RequestHeader(value = "Authorization") String authorizationHeader,
        @RequestBody EventListGetRequestDto eventListRequestDTO,
        @RequestParam Long memberId
    ) {
        String accessToken = authorizationHeader.replace("Bearer ", "");
        try {
            String from = eventListRequestDTO.from();
            String to = eventListRequestDTO.to();

            EventListGetResponseDto eventList = talkCalendarService.getEventList(accessToken, from, to);

            for (EventGetResponseDto event : eventList.events()) {
                // memberId를 사용해 저장
                eventService.saveEvent(event, memberId);
            }

            if (eventList.events().isEmpty()) {
                return ApiResponseGenerator.success(HttpStatus.NO_CONTENT, "조회된 일정이 없습니다.", eventList.events());
            }

            return ApiResponseGenerator.success(HttpStatus.OK, "일정 목록을 조회했습니다.", eventList.events());

        } catch (Exception e) {
            return ApiResponseGenerator.fail(HttpStatus.INTERNAL_SERVER_ERROR, "일정 조회 중 오류가 발생했습니다.");
        }
    }

    @PostMapping("/events")
    public ResponseEntity<?> createEvent(
        @RequestHeader(value = "Authorization") String authorizationHeader,
        @RequestBody EventCreateRequestDto eventRequestDto,
        @RequestParam Long memberId
    ) {
        String accessToken = authorizationHeader.replace("Bearer ", "");
        try {
            // 카카오 api로 일정 생성
            EventCreateResponseDto eventCreateResponseDto = talkCalendarService.createEvent(accessToken, eventRequestDto, memberId);
            return ApiResponseGenerator.success(HttpStatus.OK, "일정이 성공적으로 생성되었습니다.", eventCreateResponseDto);
        } catch (Exception e) {
            return ApiResponseGenerator.fail(HttpStatus.INTERNAL_SERVER_ERROR, "일정 생성 중 오류가 발생했습니다.");
        }
    }
}
