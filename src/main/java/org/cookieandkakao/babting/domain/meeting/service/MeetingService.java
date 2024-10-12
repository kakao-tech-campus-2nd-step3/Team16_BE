package org.cookieandkakao.babting.domain.meeting.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import org.cookieandkakao.babting.common.exception.customexception.EventCreationException;
import org.cookieandkakao.babting.common.exception.customexception.JsonConversionException;
import org.cookieandkakao.babting.common.exception.customexception.MemberNotFoundException;
import org.cookieandkakao.babting.domain.calendar.dto.response.EventCreateResponse;
import org.cookieandkakao.babting.domain.calendar.service.TalkCalendarClientService;
import org.cookieandkakao.babting.domain.meeting.dto.request.MeetingEventCreateRequest;
import org.cookieandkakao.babting.domain.meeting.dto.request.MeetingTimeCreateRequest;
import org.cookieandkakao.babting.domain.meeting.dto.request.MeetingCreateRequest;
import org.cookieandkakao.babting.domain.meeting.entity.Location;
import org.cookieandkakao.babting.domain.meeting.entity.Meeting;
import org.cookieandkakao.babting.domain.meeting.entity.MemberMeeting;
import org.cookieandkakao.babting.domain.meeting.repository.LocationRepository;
import org.cookieandkakao.babting.domain.meeting.repository.MeetingEventRepository;
import org.cookieandkakao.babting.domain.meeting.repository.MeetingRepository;
import org.cookieandkakao.babting.domain.meeting.repository.MemberMeetingRepository;
import org.cookieandkakao.babting.domain.member.entity.KakaoToken;
import org.cookieandkakao.babting.domain.member.entity.Member;
import org.cookieandkakao.babting.domain.member.repository.MemberRepository;
import org.cookieandkakao.babting.domain.member.service.MemberService;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Service
@Transactional
public class MeetingService {
    private final MeetingRepository meetingRepository;
    private final MeetingEventRepository meetingEventRepository;
    private final MemberMeetingRepository memberMeetingRepository;
    private final LocationRepository locationRepository;
    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final TalkCalendarClientService talkCalendarClientService;
    private final MemberMeetingService memberMeetingService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public MeetingService(MeetingRepository meetingRepository, MeetingEventRepository meetingEventRepository,
        MemberMeetingRepository memberMeetingRepository, LocationRepository locationRepository,
        MemberRepository memberRepository, MemberService memberService, TalkCalendarClientService talkCalendarClientService,
        MemberMeetingService memberMeetingService) {
        this.meetingRepository = meetingRepository;
        this.meetingEventRepository = meetingEventRepository;
        this.memberMeetingRepository = memberMeetingRepository;
        this.locationRepository = locationRepository;
        this.memberRepository = memberRepository;
        this.memberService = memberService;
        this.talkCalendarClientService = talkCalendarClientService;
        this.memberMeetingService = memberMeetingService;
    }

    // 모임 생성(주최자)
    public void createMeeting(Long memberId, MeetingCreateRequest meetingCreateRequest){
        Location baseLocation = meetingCreateRequest.baseLocation().toEntity();
        locationRepository.save(baseLocation);
        Meeting meeting = meetingCreateRequest.toEntity();
        meetingRepository.save(meeting);
        Member member = memberService.findMember(memberId);
        memberMeetingRepository.save(new MemberMeeting(member, meeting, true));
    }
    // 모임 시간 확정(주최자)
    // Todo 예외처리
    public void decideMeetingTime(Member member, LocalDateTime confirmDateTime, Long meetingId){
        Meeting meeting = findMeeting(meetingId);

        MemberMeeting memberMeeting = findMemberMeeting(member, meeting);

        if (!memberMeeting.isHost()){
            //Todo 예외처리
            throw new IllegalStateException("권한이 없습니다.");
        }

        if (meeting.getConfirmDateTime() != null){
            throw new IllegalStateException("이미 모임 시간이 확정되었습니다.");
        }
        
        meeting.confirmDateTime(confirmDateTime);
    }

    // 모임 참가(초대받은사람)
    public void joinMeeting(Long memberId, Long meetingId){
        Member member = memberService.findMember(memberId);
        Meeting meeting = findMeeting(meetingId);
        //Todo 초대받았는지 확인하는 로직 추가
        memberMeetingRepository.save(new MemberMeeting(member, meeting, false));
    }
    // 모임 탈퇴(주최자, 초대받은 사람)
    public void exitMeeting(Member member, Long meetingId){
        Meeting meeting = findMeeting(meetingId);
        MemberMeeting memberMeeting = findMemberMeeting(member, meeting);
        if (memberMeeting.isHost()){
            // 단일 쿼리로 삭제되는지 쿼리 여러개로 삭제되는지 확인해야됨.
            memberMeetingRepository.deleteAllByMeeting(meeting);
            //Todo 모임 삭제 시 해당 모임의 모임 일정도 전부 삭제해야함.
            meetingRepository.delete(meeting);
        } else {
            memberMeetingRepository.delete(memberMeeting);
        }
    }

    private Meeting findMeeting(Long meetingId){
        return meetingRepository.findById(meetingId)
            .orElseThrow(() -> new NoSuchElementException("해당 모임이 존재하지 않습니다."));
    }

    private MemberMeeting findMemberMeeting(Member member, Meeting meeting){
        return memberMeetingRepository.findMemberMeetingByMemberAndMeeting(member, meeting)
            .orElseThrow(() -> new NoSuchElementException("해당 모임에 회원이 존재하지 않습니다."));
    }

    private void saveMeetingEvent(Member member){
        //Todo 모임일정추가 or 계산 로직 생각중...
    }

    // 모임 확정되면 일정 생성
    public void confirmMeeting(Long memberId, Long meetingId){
        Member member = memberRepository.findById(memberId).orElseThrow( () ->
            new MemberNotFoundException("회원이 없습니다."));
        Meeting meeting = findMeeting(meetingId);
        MemberMeeting memberMeeting = findMemberMeeting(member, meeting);

        if (!memberMeeting.isHost()){
            throw new IllegalStateException("권한이 없습니다.");
        }

        if (meeting.getConfirmDateTime() != null){
            throw new IllegalStateException("이미 모임 시간이 확정되었습니다.");
        }
        /*String startAt = "2024-10-11T06:00:00Z";
        String endAt = "2024-10-11T09:00:00Z";
        String timeZone = "Asia/Seoul";
        boolean allDay = false;

        MeetingTimeCreateRequest meetingTimeCreateRequest = new MeetingTimeCreateRequest(startAt, endAt, timeZone, allDay);

        */
        MeetingEventCreateRequest meetingEventCreateRequest
            = new MeetingEventCreateRequest(
                meeting.getTitle(),
            /*meetingTimeCreateRequest*/
        );

        List<Long> memberIds = memberMeetingService.getMemberIdInMeetingId(meetingId);

        for (Long currentMemberId : memberIds){
            addMeetingEvent(currentMemberId, meetingId, meetingEventCreateRequest);
        }
    }

    // 일정 생성 후 캘린더에 일정 추가
    public EventCreateResponse addMeetingEvent(Long memberId,Long meetingId, MeetingEventCreateRequest meetingEventCreateRequest) {
        String kakaoAccessToken = getKakaoAccessToken(memberId);
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        String eventJson = convertToJSONString(meetingEventCreateRequest);
        formData.add("event", eventJson);
        Map<String, Object> responseBody = talkCalendarClientService.createEvent(kakaoAccessToken, formData);
        if (responseBody != null && responseBody.containsKey("event_id")) {
            String eventId = responseBody.get("event_id").toString();
            // EventCreateResponseDto로 응답 반환
            return new EventCreateResponse(eventId);
        }
        throw new EventCreationException("Event 생성 중 오류 발생: 응답에서 event_id가 없습니다.");
    }

    private String getKakaoAccessToken(Long memberId) {
        KakaoToken kakaoToken = memberService.getKakaoToken(memberId);
        String kakaoAccessToken = kakaoToken.getAccessToken();
        return kakaoAccessToken;
    }

    private String convertToJSONString(MeetingEventCreateRequest eventCreateRequest) {
        try {
            return objectMapper.writeValueAsString(eventCreateRequest);
        } catch (JsonProcessingException e) {
            throw new JsonConversionException("JSON 변환 중 오류가 발생했습니다.");
        }
    }

}
