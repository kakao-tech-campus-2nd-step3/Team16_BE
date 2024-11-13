package org.cookieandkakao.babting.domain.calendar.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import org.cookieandkakao.babting.common.annotaion.LoginMemberId;
import org.cookieandkakao.babting.common.apiresponse.ApiResponseBody.SuccessBody;
import org.cookieandkakao.babting.common.apiresponse.ApiResponseGenerator;
import org.cookieandkakao.babting.domain.calendar.dto.response.EventDetailGetResponse;
import org.cookieandkakao.babting.domain.calendar.dto.response.EventGetResponse;
import org.cookieandkakao.babting.domain.calendar.dto.response.EventListGetResponse;
import org.cookieandkakao.babting.domain.calendar.service.TalkCalendarService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "캘린더", description = "캘린더 관련 api입니다.")
@RestController
@RequestMapping("/api/calendar")
public class TalkCalendarController {

    private final TalkCalendarService talkCalendarService;

    public TalkCalendarController(TalkCalendarService talkCalendarService) {
        this.talkCalendarService = talkCalendarService;
    }

    @GetMapping("/events")
    @Operation(summary = "캘린더 일정 목록 조회", description = "특정 기간의 캘린더 일정 목록을 조회합니다.", security = {
        @SecurityRequirement(name = "BearerAuth")})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "일정 목록을 조회했습니다.",
            content = @Content(schema = @Schema(implementation = EventListGetResponse.class))),
        @ApiResponse(responseCode = "200", description = "조회된 일정 목록이 없습니다.",
            content = @Content(schema = @Schema(implementation = EventListGetResponse.class)))
    })
    public ResponseEntity<SuccessBody<EventListGetResponse>> getEventList(
        @Parameter(description = "조회 시작 날짜 (yyyy-MM-dd'T'HH:mm:ss'Z' 형식)", required = true) @RequestParam
        @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}Z") String from,
        @Parameter(description = "조회 종료 날짜 (yyyy-MM-dd'T'HH:mm:ss'Z' 형식)", required = true) @RequestParam
        @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}Z") String to,
        @LoginMemberId Long memberId
    ) {

        List<EventGetResponse> updatedEvents = talkCalendarService.getUpdatedEventList(from, to,
            memberId);
        EventListGetResponse eventList = new EventListGetResponse(updatedEvents);

        if (updatedEvents.isEmpty()) {
            return ApiResponseGenerator.success("조회된 일정 목록이 없습니다.", eventList);
        }

        return ApiResponseGenerator.success("일정 목록을 조회했습니다.", eventList);
    }

    @GetMapping("/events/{event_id}")
    @Operation(summary = "캘린더 일정 상세 조회", description = "특정 캘린더 일정의 세부 정보를 조회합니다.", security = {
        @SecurityRequirement(name = "BearerAuth")})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "일정을 조회했습니다.",
            content = @Content(schema = @Schema(implementation = EventDetailGetResponse.class)))})
    public ResponseEntity<SuccessBody<EventDetailGetResponse>> getEvent(
        @Parameter(description = "조회할 일정 ID", required = true) @PathVariable("event_id") String eventId,
        @LoginMemberId Long memberId
    ) {
        EventDetailGetResponse eventDetailGetResponse = talkCalendarService.getEvent(memberId,
            eventId);

        return ApiResponseGenerator.success("일정을 조회했습니다.",
            eventDetailGetResponse);
    }
}
