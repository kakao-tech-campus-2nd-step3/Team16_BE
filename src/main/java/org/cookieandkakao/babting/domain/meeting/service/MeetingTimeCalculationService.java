package org.cookieandkakao.babting.domain.meeting.service;

import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.cookieandkakao.babting.domain.calendar.dto.response.EventGetResponse;
import org.cookieandkakao.babting.domain.calendar.dto.response.TimeGetResponse;
import org.cookieandkakao.babting.domain.calendar.entity.Time;
import org.cookieandkakao.babting.domain.calendar.service.TalkCalendarService;
import org.cookieandkakao.babting.domain.meeting.dto.response.TimeAvailableGetResponse;
import org.cookieandkakao.babting.domain.meeting.dto.response.TimeSlot;
import org.cookieandkakao.babting.domain.meeting.entity.Meeting;
import org.cookieandkakao.babting.domain.meeting.entity.TimeZone;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class MeetingTimeCalculationService {
    private final MeetingService meetingService;
    private final TalkCalendarService talkCalendarService;

    public MeetingTimeCalculationService(MeetingService meetingService,
        TalkCalendarService talkCalendarService) {
        this.meetingService = meetingService;
        this.talkCalendarService = talkCalendarService;
    }

    /** 빈 시간대 조회 로직 설명
     *
     * 1. 모임의 모든 참여자들의 일정 중 Time을 allTimes에 추출
     * 2. allTimes의 시간을 시작 시간을 기준으로 오름차순 정렬한 값들을 sortedTimes에 저장
     * 3. sortedTimes에 겹치는 시간이 있다면 모든 시간들을 (2024-10-24T15:00 ~ 2024-10-24T16:00)
     *  ex) 1. 2024-10-24T12:00 ~ 2024-10-24T15:00
     *      2. 2024-10-24T14:00 ~ 2024-10-24T16:00
     *      1의 시간을 1의 시작 시간 ~ 2의 끝 시간으로 병합 (2024-10-24T12:00 ~ 2024-10-24T16:00)
     * 4. 이제 mergedTime에는 겹치지 않는 시간대만 존재
     * 5. mergedTime에 있는 시간들을 순회하면서 i번째 끝 시간 ~ i+1번째 시작 시간으로 시간 생성
     */
    public TimeAvailableGetResponse findAvailableTime(Long meetingId) {
        List<Long> joinedMemberIds = meetingService.getMemberIdInMeetingId(meetingId);
        Meeting meeting = meetingService.findMeeting(meetingId);
        LocalDateTime from = meeting.getStartDate().atTime(meeting.getStartTime());
        LocalDateTime to = meeting.getEndDate().atTime(meeting.getEndTime());

        // 참여자별 일정에서 필요한 시간 정보만 추출하여 리스트로 수집
        List<TimeGetResponse> allTimes = joinedMemberIds.stream()
            .flatMap(memberId ->
                talkCalendarService
                    .getUpdatedEventList(from.toString(), to.toString(), memberId)
                    .stream()
                    .map(EventGetResponse::time)
            )
            .toList();

        // Todo 모임만의 일정도 추가

        // 시간대 정렬 (시작 시간을 기준으로 오름차순 정렬)
        List<TimeGetResponse> sortedTimes = allTimes.stream()
            .sorted(Comparator.comparing(time -> LocalDateTime.parse(time.startAt())))
            .toList();

        // 겹치는 시간 병합
        List<TimeSlot> mergedTimes = mergeOverlappingTimes(sortedTimes);

        // 빈 시간대 계산
        List<TimeSlot> availableTime = calculateAvailableTimes(mergedTimes, from, to);

        return TimeAvailableGetResponse.of(meeting, availableTime);
    }

    // 겹치는 시간 병합
    public List<TimeSlot> mergeOverlappingTimes(List<TimeGetResponse> times) {
        List<TimeSlot> mergedTimes = new ArrayList<>();

        if (times.isEmpty()) {
            return mergedTimes;
        }

        TimeSlot currentTime = TimeSlot.toTimeSlot(times.getFirst());


        for (int i = 1; i < times.size(); i++) {
            TimeSlot next = TimeSlot.toTimeSlot(times.get(i));
            LocalDateTime currentEnd = currentTime.endAt();
            LocalDateTime nextStart = next.startAt();

            // 겹치는 시간대라면 병합
            if (!nextStart.isAfter(currentEnd)) {
                currentTime = new TimeSlot(
                    currentTime.startAt(),
                    maxEndTime(currentTime.endAt(), next.endAt()),
                    currentTime.timeZone(),
                    currentTime.allDay() || next.allDay()
                );
            } else {
                mergedTimes.add(currentTime);
                currentTime = next;
            }
        }

        mergedTimes.add(currentTime); // 마지막 시간대 추가
        return mergedTimes;
    }

    // 두 시간대 중 더 늦은 종료 시간을 반환
    public LocalDateTime maxEndTime(LocalDateTime end1, LocalDateTime end2) {
        if (end1.isAfter(end2)) {
            return end1;
        }
        return end2;
    }

    // 빈 시간대
    public List<TimeSlot> calculateAvailableTimes(List<TimeSlot> mergedTimes,
        LocalDateTime from, LocalDateTime to) {
        List<TimeSlot> availableTimes = new ArrayList<>();

        // 첫 번째 시간대 이전의 빈 시간 확인
        // => 검색 시작일 ~ mergedTime 첫번째 일정의 시작시간까지 빈 시간
        if (from.isBefore(mergedTimes.getFirst().startAt())) {
            availableTimes.add(new TimeSlot(
                from,
                mergedTimes.getFirst().startAt(),
                TimeZone.SEOUL.getArea(),
                false
            ));
        }

        // 두 시간대 사이의 빈 시간 계산
        for (int i = 0; i < mergedTimes.size() - 1; i++) {
            LocalDateTime endOfCurrent = mergedTimes.get(i).endAt();
            LocalDateTime startOfNext = mergedTimes.get(i + 1).startAt();

            // 첫 번째 time의 끝 시간 ~ 두 번째 time의 시작시간 => 빈 시간대
            if (endOfCurrent.isBefore(startOfNext)) {
                availableTimes.add(new TimeSlot(
                    mergedTimes.get(i).endAt(),
                    mergedTimes.get(i + 1).startAt(),
                    TimeZone.SEOUL.getArea(),
                    false
                ));
            }
        }

        // 마지막 시간대 이후의 빈 시간 확인
        if (to.isAfter(mergedTimes.getLast().endAt())) {
            availableTimes.add(new TimeSlot(
                mergedTimes.getLast().endAt(),
                to,
                TimeZone.SEOUL.getArea(),
                false
            ));
        }

        return availableTimes;
    }
}
