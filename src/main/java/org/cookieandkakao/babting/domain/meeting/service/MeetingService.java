package org.cookieandkakao.babting.domain.meeting.service;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import org.cookieandkakao.babting.domain.meeting.dto.request.LocationCreateRequest;
import org.cookieandkakao.babting.domain.meeting.dto.request.MeetingCreateRequest;
import org.cookieandkakao.babting.domain.meeting.entity.Location;
import org.cookieandkakao.babting.domain.meeting.entity.Meeting;
import org.cookieandkakao.babting.domain.meeting.entity.MemberMeeting;
import org.cookieandkakao.babting.domain.meeting.repository.LocationRepository;
import org.cookieandkakao.babting.domain.meeting.repository.MeetingEventRepository;
import org.cookieandkakao.babting.domain.meeting.repository.MeetingRepository;
import org.cookieandkakao.babting.domain.meeting.repository.MemberMeetingRepository;
import org.cookieandkakao.babting.domain.member.entity.Member;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class MeetingService {
    private final MeetingRepository meetingRepository;
    private final MeetingEventRepository meetingEventRepository;
    private final MemberMeetingRepository memberMeetingRepository;
    private final LocationRepository locationRepository;

    public MeetingService(MeetingRepository meetingRepository, MeetingEventRepository meetingEventRepository,
        MemberMeetingRepository memberMeetingRepository, LocationRepository locationRepository) {
        this.meetingRepository = meetingRepository;
        this.meetingEventRepository = meetingEventRepository;
        this.memberMeetingRepository = memberMeetingRepository;
        this.locationRepository = locationRepository;
    }

    // 모임 생성(주최자)
    public void createMeeting(Member member, MeetingCreateRequest meetingCreateRequest){
        Meeting meeting = meetingCreateRequest.toEntity();
        Location baseLocation = meetingCreateRequest.baseLocation().toEntity();

        locationRepository.save(baseLocation);
        meetingRepository.save(meeting);
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
    public void joinMeeting(Member member, Long meetingId){
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
}
