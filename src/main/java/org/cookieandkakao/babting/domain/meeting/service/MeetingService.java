package org.cookieandkakao.babting.domain.meeting.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import org.cookieandkakao.babting.domain.food.entity.Food;
import org.cookieandkakao.babting.domain.food.service.FoodRepositoryService;
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
import org.cookieandkakao.babting.domain.meeting.dto.response.MeetingGetResponse;
import org.cookieandkakao.babting.domain.meeting.entity.Location;
import org.cookieandkakao.babting.domain.meeting.entity.Meeting;
import org.cookieandkakao.babting.domain.meeting.entity.MeetingEvent;
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
    private final FoodRepositoryService foodRepositoryService;
    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final TalkCalendarClientService talkCalendarClientService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public MeetingService(MeetingRepository meetingRepository,
        MeetingEventRepository meetingEventRepository,
        MemberMeetingRepository memberMeetingRepository,
        LocationRepository locationRepository, FoodRepositoryService foodRepositoryService,
        MemberService memberService, MemberRepository memberRepository,
        TalkCalendarClientService talkCalendarClientService) {
        this.meetingRepository = meetingRepository;
        this.meetingEventRepository = meetingEventRepository;
        this.memberMeetingRepository = memberMeetingRepository;
        this.locationRepository = locationRepository;
        this.foodRepositoryService = foodRepositoryService;
        this.memberService = memberService;
        this.memberRepository = memberRepository;
        this.talkCalendarClientService = talkCalendarClientService;
    }

    // 모임 생성(주최자)
    public void createMeeting(Long memberId, MeetingCreateRequest meetingCreateRequest){
        Member member = memberService.findMember(memberId);
        Meeting meeting = meetingCreateRequest.toEntity();
        Location baseLocation = meetingCreateRequest.baseLocation().toEntity();
        locationRepository.save(baseLocation);
        meetingRepository.save(meeting);
        memberMeetingRepository.save(new MemberMeeting(member, meeting, true));
    }
    // 모임 확정(주최자)
    public void decideMeeting(Long memberId, Long confirmFoodId, LocalDateTime confirmDateTime, Long meetingId){
        Member member = memberService.findMember(memberId);
        Meeting meeting = findMeeting(meetingId);

        MemberMeeting memberMeeting = findMemberMeeting(member, meeting);

        if (!memberMeeting.isHost()){
            throw new IllegalStateException("권한이 없습니다.");
        }

        if (meeting.getConfirmDateTime() != null){
            throw new IllegalStateException("이미 모임 시간이 확정되었습니다.");
        }

        if (confirmFoodId != null){
            Food food = foodRepositoryService.findFoodById(confirmFoodId);
            meeting.confirmFood(food);
        }
        meeting.confirmDateTime(confirmDateTime);
    }

    // 모임 참가(초대받은사람)
    public void joinMeeting(Long memberId, Long meetingId){
        Member member = memberService.findMember(memberId);
        Meeting meeting = findMeeting(meetingId);

        boolean isJoinMeeting = memberMeetingRepository.existsByMemberAndMeeting(member, meeting);
        if (isJoinMeeting){
            throw new IllegalStateException("이미 모임에 참가한 상태입니다.");
        }

        // Todo 모임 참가시 모임 선호/비선호 음식 추가
        memberMeetingRepository.save(new MemberMeeting(member, meeting, false));
    }
    // 모임 탈퇴(주최자, 초대받은 사람)
    public void exitMeeting(Long memberId, Long meetingId){
        Member member = memberService.findMember(memberId);
        Meeting meeting = findMeeting(meetingId);
        MemberMeeting memberMeeting = findMemberMeeting(member, meeting);

        // 모임 확정 전
        if (meeting.getConfirmDateTime() != null){
            if (memberMeeting.isHost()){
                // 해당 모임에 속하는 회원 모임 전부 삭제
                memberMeetingRepository.deleteAllByMeeting(meeting);
                // 모임 삭제
                meetingRepository.delete(meeting);
            } else {
                // 해당 모임에 속하는 회원 모임 삭제
                memberMeetingRepository.delete(memberMeeting);
            }
        // 모임 확정 후
        } else{
            //Todo 모임 삭제 시 해당 모임의 모임 일정도 전부 삭제해야함.
            //만약 톡 캘린더에서 일정을 삭제했을 시 모임 일정이 없을 수도 있음.
            if (memberMeeting.isHost()){
                // 해당 모임의 모든 모임 일정 삭제
                // 해당 모임에 속하는 회원 모임 전부 삭제
                // 모임 삭제
            } else {
                // 해당 모임의 일정 삭제
                // 해당 모임에 속하는 회원 모임 전부 삭제
            }
        }

    }

    // 모임 목록 조회
    public List<MeetingGetResponse> getAllMeetings(Long memberId){
        Member member = memberService.findMember(memberId);
        List<Meeting> meetingList = memberMeetingRepository.findMeetingsByMember(member);
        return meetingList.stream()
            .map(MeetingGetResponse::from)
            .collect(Collectors.toList());
    }
  
    public Meeting findMeeting(Long meetingId){
        return meetingRepository.findById(meetingId)
            .orElseThrow(() -> new NoSuchElementException("해당 모임이 존재하지 않습니다."));
    }

    public MemberMeeting findMemberMeeting(Member member, Meeting meeting){
        return memberMeetingRepository.findByMemberAndMeeting(member, meeting)
            .orElseThrow(() -> new NoSuchElementException("해당 모임에 회원이 존재하지 않습니다."));
    }

    // MeetingEvent는 없을 수 있기 때문에 Optional로 반환
    private Optional<MeetingEvent> findMeetingEvent(MemberMeeting memberMeeting){
        return meetingEventRepository.findByMemberMeeting(memberMeeting);
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
                // conflict 해결하느라 null 값 입력
                meeting.getTitle(), null
            /*meetingTimeCreateRequest*/
        );

        List<Long> memberIds = getMemberIdInMeetingId(meetingId);

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

    public List<Long> getMemberIdInMeetingId(Long meetingId) {
        Meeting meeting = meetingRepository.findById(meetingId)
            .orElseThrow(() -> new IllegalArgumentException("해당 모임이 존재하지 않습니다."));

        List<MemberMeeting> memberMeetings = memberMeetingRepository.findByMeeting(meeting)
            .orElseThrow(() -> new IllegalArgumentException("회원이 없습니다."));
        return memberMeetings.stream()
            .map(memberMeeting -> memberMeeting.getMember().getMemberId())
            .toList();
    }

}
