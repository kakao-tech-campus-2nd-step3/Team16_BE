package org.cookieandkakao.babting.domain.meeting.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.cookieandkakao.babting.common.exception.customexception.EventCreationException;
import org.cookieandkakao.babting.common.exception.customexception.JsonConversionException;
import org.cookieandkakao.babting.domain.calendar.dto.response.EventCreateResponse;
import org.cookieandkakao.babting.domain.calendar.dto.response.EventGetResponse;
import org.cookieandkakao.babting.domain.calendar.dto.response.TimeGetResponse;
import org.cookieandkakao.babting.domain.calendar.service.TalkCalendarClientService;
import org.cookieandkakao.babting.domain.meeting.dto.request.ConfirmMeetingGetRequest;
import org.cookieandkakao.babting.domain.meeting.dto.request.MeetingEventCreateRequest;
import org.cookieandkakao.babting.domain.meeting.dto.request.MeetingTimeCreateRequest;
import org.cookieandkakao.babting.domain.meeting.entity.Meeting;
import org.cookieandkakao.babting.domain.meeting.entity.MemberMeeting;
import org.cookieandkakao.babting.domain.member.entity.KakaoToken;
import org.cookieandkakao.babting.domain.member.entity.Member;
import org.cookieandkakao.babting.domain.member.service.MemberService;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Transactional
@Service
public class MeetingEventService {
    private final MemberService memberService;
    private final TalkCalendarClientService talkCalendarClientService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final MeetingService meetingService;
    private static final String TIME_ZONE = "Asia/Seoul";

    public MeetingEventService(MemberService memberService,
        TalkCalendarClientService talkCalendarClientService,
        MeetingService meetingService) {
        this.memberService = memberService;
        this.talkCalendarClientService = talkCalendarClientService;
        this.meetingService = meetingService;
    }

    // 모임 확정되면 일정 생성
    public void confirmMeeting(Long memberId, Long meetingId, ConfirmMeetingGetRequest confirmMeetingGetRequest){
        Member member = memberService.findMember(memberId);
        Meeting meeting = meetingService.findMeeting(meetingId);
        MemberMeeting memberMeeting = meetingService.findMemberMeeting(member, meeting);

        if (!memberMeeting.isHost()){
            throw new IllegalStateException("권한이 없습니다.");
        }

        if (meeting.getConfirmDateTime() != null){
            throw new IllegalStateException("이미 모임 시간이 확정되었습니다.");
        }

        meeting.confirmDateTime(confirmMeetingGetRequest.confirmDateTime());

        String startAt = meeting.getConfirmDateTime()
            .minusHours(9)
            .toString();
        String endAt = meeting.getConfirmDateTime().minusHours(9).plusMinutes(meeting.getDurationTime()).toString();
        boolean allDay = false;

        MeetingTimeCreateRequest meetingTimeCreateRequest =
            new MeetingTimeCreateRequest(startAt, endAt, TIME_ZONE, allDay);

        MeetingEventCreateRequest meetingEventCreateRequest
            = new MeetingEventCreateRequest(
            meeting.getTitle(), meetingTimeCreateRequest,
            List.of(15,30)
        );

        List<Long> memberIds = getMemberIdInMeetingId(meetingId);

        for (Long currentMemberId : memberIds){
            addMeetingEvent(currentMemberId, meetingEventCreateRequest);
        }
    }

    // 일정 생성 후 캘린더에 일정 추가
    public EventCreateResponse addMeetingEvent(Long memberId, MeetingEventCreateRequest meetingEventCreateRequest) {
        String kakaoAccessToken = getKakaoAccessToken(memberId);
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        String eventJson = convertToJSONString(meetingEventCreateRequest);
        formData.add("event", eventJson);
        EventCreateResponse responseBody = talkCalendarClientService.createEvent(kakaoAccessToken, formData);
        if (responseBody != null) {
            return responseBody;
        }
        throw new EventCreationException("Event 생성 중 오류 발생: 응답에서 event_id가 없습니다.");
    }

    private String getKakaoAccessToken(Long memberId) {
        KakaoToken kakaoToken = memberService.getKakaoToken(memberId);
        return kakaoToken.getAccessToken();
    }

    private String convertToJSONString(MeetingEventCreateRequest eventCreateRequest) {
        try {
            return objectMapper.writeValueAsString(eventCreateRequest);
        } catch (JsonProcessingException e) {
            throw new JsonConversionException("JSON 변환 중 오류가 발생했습니다.");
        }
    }

    private List<Long> getMemberIdInMeetingId(Long meetingId) {
        Meeting meeting = meetingService.findMeeting(meetingId);
        List<MemberMeeting> memberMeetings = meetingService.findAllMemberMeeting(meeting);

        return memberMeetings.stream()
            .map(memberMeeting -> memberMeeting.getMember().getMemberId())
            .toList();
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
    public List<TimeGetResponse> findAvailableTime(Long meetingId, String from, String to) {
        List<Long> joinedMemberIds = getMemberIdInMeetingId(meetingId);

        // 참여자별 일정에서 필요한 시간 정보만 추출하여 리스트로 수집
        List<TimeGetResponse> allTimes = joinedMemberIds.stream()
            .flatMap(memberId ->
                talkCalendarClientService
                    .getEventList(getKakaoAccessToken(memberId), from, to)
                    .events()
                    .stream()
                    .map(EventGetResponse::time)
            )
            .toList();

        // 시간대 정렬 (시작 시간을 기준으로 오름차순 정렬)
        List<TimeGetResponse> sortedTimes = allTimes.stream()
            .sorted(Comparator.comparing(time -> LocalDateTime.parse(time.startAt())))
            .toList();

        // 겹치는 시간 병합
        List<TimeGetResponse> mergedTimes = mergeOverlappingTimes(sortedTimes);

        // 빈 시간대 계산
        return calculateAvailableTimes(mergedTimes, from, to);
    }

    // 겹치는 시간 병합
    private List<TimeGetResponse> mergeOverlappingTimes(List<TimeGetResponse> times) {
        List<TimeGetResponse> mergedTimes = new ArrayList<>();

        if (times.isEmpty())
            return mergedTimes;

        TimeGetResponse currentTime = times.getFirst();

        for (int i = 1; i < times.size(); i++) {
            TimeGetResponse next = times.get(i);
            LocalDateTime currentEnd = LocalDateTime.parse(currentTime.endAt());
            LocalDateTime nextStart = LocalDateTime.parse(next.startAt());

            // 겹치는 시간대라면 병합
            if (!nextStart.isAfter(currentEnd)) {
                currentTime = new TimeGetResponse(
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
    private String maxEndTime(String end1, String end2) {
        LocalDateTime e1 = LocalDateTime.parse(end1);
        LocalDateTime e2 = LocalDateTime.parse(end2);
        if (e1.isAfter(e2)){
            return end1;
        }
        return end2;
    }

    // 빈 시간대
    private List<TimeGetResponse> calculateAvailableTimes(List<TimeGetResponse> mergedTimes, String from, String to) {
        List<TimeGetResponse> availableTimes = new ArrayList<>();
        LocalDateTime searchStart = LocalDateTime.parse(from);
        LocalDateTime searchEnd = LocalDateTime.parse(to);

        // 첫 번째 시간대 이전의 빈 시간 확인
        // => 검색 시작일 ~ mergedTime 첫번째 일정의 시작시간까지 빈 시간
        if (searchStart.isBefore(LocalDateTime.parse(mergedTimes.getFirst().startAt()))) {
            availableTimes.add(new TimeGetResponse(
                from,
                mergedTimes.getFirst().startAt(),
                TIME_ZONE,
                false
            ));
        }

        // 두 시간대 사이의 빈 시간 계산
        for (int i = 0; i < mergedTimes.size() - 1; i++) {
            LocalDateTime endOfCurrent = LocalDateTime.parse(mergedTimes.get(i).endAt());
            LocalDateTime startOfNext = LocalDateTime.parse(mergedTimes.get(i + 1).startAt());

            // 첫 번째 time의 끝 시간 ~ 두 번째 time의 시작시간 => 빈 시간대
            if (endOfCurrent.isBefore(startOfNext)) {
                availableTimes.add(new TimeGetResponse(
                    mergedTimes.get(i).endAt(),
                    mergedTimes.get(i + 1).startAt(),
                    TIME_ZONE,
                    false
                ));
            }
        }

        // 마지막 시간대 이후의 빈 시간 확인
        if (searchEnd.isAfter(LocalDateTime.parse(mergedTimes.getLast().endAt()))) {
            availableTimes.add(new TimeGetResponse(
                mergedTimes.getLast().endAt(),
                to,
                TIME_ZONE,
                false
            ));
        }

        return availableTimes;
    }



}
