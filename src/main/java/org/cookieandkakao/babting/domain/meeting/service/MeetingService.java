package org.cookieandkakao.babting.domain.meeting.service;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import org.cookieandkakao.babting.domain.meeting.dto.request.MeetingCreateRequest;
import org.cookieandkakao.babting.domain.meeting.dto.request.MeetingUpdateRequest;
import org.cookieandkakao.babting.domain.meeting.dto.response.MeetingConfirmedInfoResponse;
import org.cookieandkakao.babting.domain.meeting.dto.response.MeetingGetResponse;
import org.cookieandkakao.babting.domain.meeting.dto.response.MeetingHostCheckResponse;
import org.cookieandkakao.babting.domain.meeting.dto.response.MeetingInfoGetResponse;
import org.cookieandkakao.babting.domain.meeting.entity.Location;
import org.cookieandkakao.babting.domain.meeting.entity.Meeting;
import org.cookieandkakao.babting.domain.meeting.entity.MemberMeeting;
import org.cookieandkakao.babting.domain.meeting.exception.meeting.MeetingAlreadyJoinException;
import org.cookieandkakao.babting.domain.meeting.exception.meeting.MeetingHostUnauthorizedException;
import org.cookieandkakao.babting.domain.meeting.exception.meeting.MeetingNotFoundException;
import org.cookieandkakao.babting.domain.meeting.exception.membermeeting.MemberMeetingNotFoundException;
import org.cookieandkakao.babting.domain.meeting.repository.LocationRepository;
import org.cookieandkakao.babting.domain.meeting.repository.MeetingRepository;
import org.cookieandkakao.babting.domain.meeting.repository.MemberMeetingRepository;
import org.cookieandkakao.babting.domain.member.entity.Member;
import org.cookieandkakao.babting.domain.member.service.MemberService;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class MeetingService {
    private final MeetingRepository meetingRepository;
    private final MemberMeetingRepository memberMeetingRepository;
    private final LocationRepository locationRepository;
    private final MemberService memberService;


    public MeetingService(MeetingRepository meetingRepository,
        MemberMeetingRepository memberMeetingRepository,
        LocationRepository locationRepository,
        MemberService memberService) {
        this.meetingRepository = meetingRepository;
        this.memberMeetingRepository = memberMeetingRepository;
        this.locationRepository = locationRepository;
        this.memberService = memberService;
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

    // 모임 수정
    public void updateMeeting(Long memberId, Long meetingId, MeetingUpdateRequest meetingUpdateRequest) {
        Member member = memberService.findMember(memberId);
        Meeting meeting = findMeeting(meetingId);
        MemberMeeting memberMeeting = findMemberMeeting(member, meeting);

        Location baseLocation = meetingUpdateRequest.baseLocation().toEntity();

        if (!memberMeeting.isHost()){
            throw new MeetingHostUnauthorizedException("주최자만 모임을 수정할 수 있습니다.");
        }
        
        meeting.updateBaseLocation(baseLocation);
        meeting.updateTitle(meetingUpdateRequest.title());
        meeting.updateStartDate(meetingUpdateRequest.startDate());
        meeting.updateEndDate(meetingUpdateRequest.endDate());
        meeting.updateDurationTime(meetingUpdateRequest.durationTime());
        meeting.updateStartTime(meetingUpdateRequest.startTime());
        meeting.updateEndTime(meetingUpdateRequest.endTime());
    }

    // 모임 참가(초대받은사람)
    public void joinMeeting(Long memberId, Long meetingId){
        Member member = memberService.findMember(memberId);
        Meeting meeting = findMeeting(meetingId);

        boolean isJoinMeeting = memberMeetingRepository.existsByMemberAndMeeting(member, meeting);
        if (isJoinMeeting){
            throw new MeetingAlreadyJoinException("이미 모임에 참가한 상태입니다.");
        }

        memberMeetingRepository.save(new MemberMeeting(member, meeting, false));
    }

    // 모임 탈퇴(주최자, 초대받은 사람)
    public void exitMeeting(Long memberId, Long meetingId){
        Member member = memberService.findMember(memberId);
        Meeting meeting = findMeeting(meetingId);
        MemberMeeting memberMeeting = findMemberMeeting(member, meeting);

        if (memberMeeting.isHost()){
            // 해당 모임에 속하는 회원 모임 전부 삭제
            memberMeetingRepository.deleteAllByMeeting(meeting);
            // 모임 삭제
            meetingRepository.delete(meeting);
        } else {
            // 해당 모임에 속하는 회원 모임 삭제
            memberMeetingRepository.delete(memberMeeting);
        }
    }

    // 내가 참여한 모임 목록 조회
    public List<MeetingGetResponse> getAllMeetings(Long memberId){
        Member member = memberService.findMember(memberId);
        List<Meeting> meetingList = memberMeetingRepository.findMeetingsByMember(member);
        return meetingList.stream()
            .map(MeetingGetResponse::from)
            .collect(Collectors.toList());
    }

    // 모임 이름 조회, 시작과 끝 날짜 조회
    public MeetingInfoGetResponse getMeetingInfo(Long meetingId){
        Meeting meeting = findMeeting(meetingId);
        return MeetingInfoGetResponse.from(meeting);
    }

    // 모임 확정 날짜, 확정 음식 확인
    public MeetingConfirmedInfoResponse getMeetingConfirmedInfo(Long meetingId){
        Meeting meeting = findMeeting(meetingId);
        return MeetingConfirmedInfoResponse.of(meeting);
    }

    // 주최자 확인
    public MeetingHostCheckResponse checkHost(Long memberId, Long meetingId){
        Member member = memberService.findMember(memberId);
        Meeting meeting = findMeeting(meetingId);
        MemberMeeting memberMeeting = findMemberMeeting(member, meeting);
        return new MeetingHostCheckResponse(memberMeeting.isHost());
    }
    public Meeting findMeeting(Long meetingId){
        return meetingRepository.findById(meetingId)
            .orElseThrow(() -> new MeetingNotFoundException("해당 모임이 존재하지 않습니다."));
    }

    public MemberMeeting findMemberMeeting(Member member, Meeting meeting){
        return memberMeetingRepository.findByMemberAndMeeting(member, meeting)
            .orElseThrow(() -> new MemberMeetingNotFoundException("해당 모임에 회원이 존재하지 않습니다."));
    }

    public List<MemberMeeting> findAllMemberMeeting(Meeting meeting){
        return memberMeetingRepository.findByMeeting(meeting);
    }

    public List<Long> getMemberIdInMeetingId(Long meetingId) {
        Meeting meeting = findMeeting(meetingId);
        List<MemberMeeting> memberMeetings = findAllMemberMeeting(meeting);

        return memberMeetings.stream()
            .map(memberMeeting -> memberMeeting.getMember().getMemberId())
            .toList();
    }
}
