package org.cookieandkakao.babting.domain.meeting.controller.validation;

import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import java.util.stream.Stream;
import org.cookieandkakao.babting.domain.meeting.dto.request.LocationCreateRequest;
import org.cookieandkakao.babting.domain.meeting.dto.request.MeetingCreateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class MeetingCreateValidationTest {

    private Validator validator;
    private LocationCreateRequest baseLocation;
    private LocalDate now;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer durationTime;
    private LocalTime startTime;
    private LocalTime endTime;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();

        baseLocation = new LocationCreateRequest("전대", "11", 1.1, 1.1);
        now = LocalDate.now();
        title = "밥팅";
        startDate = now.plusDays(1);
        endDate = now.plusDays(2);
        durationTime = 3;
        startTime = LocalTime.of(14, 0);
        endTime = LocalTime.of(17, 0);
    }

    @Test
    void 모임의_시작_시간은_현재_시간보다_빠를_수_없다() {
        //given
        startDate = now.minusDays(1);

        MeetingCreateRequest meetingCreateRequest = new MeetingCreateRequest(baseLocation, title,
            startDate, endDate, durationTime, startTime, endTime);
        //when
        Set<ConstraintViolation<MeetingCreateRequest>> validate = validator.validate(
            meetingCreateRequest);
        //then
        assertTrue(
            validate.stream().anyMatch(v -> v.getPropertyPath().toString().equals("startDate")),
            "startDate" + " 유효성 검사 실패");
    }

    @Test
    void 모임의_끝_시간은_현재_시간보다_빠를_수_없다() {
        //given
        endDate = now.minusDays(1);

        MeetingCreateRequest meetingCreateRequest = new MeetingCreateRequest(baseLocation, title,
            startDate, endDate, durationTime, startTime, endTime);
        //when
        Set<ConstraintViolation<MeetingCreateRequest>> validate = validator.validate(
            meetingCreateRequest);
        //then
        assertTrue(
            validate.stream().anyMatch(v -> v.getPropertyPath().toString().equals("endDate")),
            "endDate" + " 유효성 검사 실패");
    }

    @ParameterizedTest
    @MethodSource("nullArgument")
    void 모임의_모든_정보가_입력되어야한다(MeetingCreateRequest request, String fieldName) {
        // when
        Set<ConstraintViolation<MeetingCreateRequest>> violations = validator.validate(request);

        // then
        assertTrue(
            violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals(fieldName)),
            fieldName + " 유효성 검사 실패");
    }

    private static Stream<Arguments> nullArgument() {
        LocationCreateRequest baseLocation = new LocationCreateRequest("전대", "11", 1.1, 1.1);
        LocalDate now = LocalDate.now();
        String title = "밥팅";
        LocalDate startDate = now.plusDays(1);
        LocalDate endDate = now.plusDays(2);
        Integer durationTime = 3;
        LocalTime startTime = LocalTime.of(14, 0);
        LocalTime endTime = LocalTime.of(17, 0);

        return Stream.of(
            Arguments.of(new MeetingCreateRequest(null, title, startDate, endDate, durationTime, startTime, endTime), "baseLocation"),
            Arguments.of(new MeetingCreateRequest(baseLocation, null, startDate, endDate, durationTime, startTime, endTime), "title"),
            Arguments.of(new MeetingCreateRequest(baseLocation, title, null, endDate, durationTime, startTime, endTime), "startDate"),
            Arguments.of(new MeetingCreateRequest(baseLocation, title, startDate, null, durationTime, startTime, endTime), "endDate"),
            Arguments.of(new MeetingCreateRequest(baseLocation, title, startDate, endDate, null, startTime, endTime), "durationTime"),
            Arguments.of(new MeetingCreateRequest(baseLocation, title, startDate, endDate, durationTime, null, endTime), "startTime"),
            Arguments.of(new MeetingCreateRequest(baseLocation, title, startDate, endDate, durationTime, startTime, null), "endTime")
        );
    }
}