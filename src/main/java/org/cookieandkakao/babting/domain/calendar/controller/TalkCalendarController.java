package org.cookieandkakao.babting.domain.calendar.controller;

import java.util.Collections;
import org.cookieandkakao.babting.domain.calendar.dto.EventListDTO;
import org.cookieandkakao.babting.domain.calendar.service.TalkCalendarService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/calendar")
public class TalkCalendarController {

    private final TalkCalendarService talkCalendarService;

    public TalkCalendarController(TalkCalendarService talkCalendarService) {
        this.talkCalendarService = talkCalendarService;
    }

    @GetMapping("/events")
    public ResponseEntity<EventListDTO> getEventList(
        @RequestHeader(value = "Authorization") String authorizationHeader,
        @RequestParam(value = "from") String from,
        @RequestParam(value = "to") String to
    ) {
        String accessToken = authorizationHeader.replace("Bearer ", "");
        try {
            EventListDTO eventList = talkCalendarService.getEventList(accessToken, from, to);

            HttpStatus status = HttpStatus.OK;
            String message = "일정 목록을 조회했습니다.";

            if (eventList.events().isEmpty()) {
                status = HttpStatus.NO_CONTENT;
                message = "조회된 일정이 없습니다.";
            }

            EventListDTO responseBody = new EventListDTO(
                status.value(),
                message,
                eventList.events(),
                eventList.hasNext()
            );

            return ResponseEntity.status(status).body(responseBody);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new EventListDTO(
                    500,
                    "일정 조회 중 오류가 발생했습니다.",
                    Collections.emptyList(),
                    false
                ));
        }
    }
}
