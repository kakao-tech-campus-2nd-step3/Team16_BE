package org.cookieandkakao.babting.domain.meeting.service;

import java.util.List;
import org.cookieandkakao.babting.common.exception.customexception.MemberNotFoundException;
import org.cookieandkakao.babting.domain.meeting.entity.Meeting;
import org.cookieandkakao.babting.domain.meeting.entity.MemberMeeting;
import org.cookieandkakao.babting.domain.meeting.repository.MeetingRepository;
import org.cookieandkakao.babting.domain.meeting.repository.MemberMeetingRepository;
import org.cookieandkakao.babting.domain.member.entity.Member;
import org.cookieandkakao.babting.domain.member.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MemberMeetingService {

    private final MemberMeetingRepository memberMeetingRepository;
    private final MemberRepository memberRepository;
    private final MeetingRepository meetingRepository;


    public MemberMeetingService(MemberMeetingRepository memberMeetingRepository,
        MemberRepository memberRepository, MeetingRepository meetingRepository) {
        this.memberMeetingRepository = memberMeetingRepository;
        this.memberRepository = memberRepository;
        this.meetingRepository = meetingRepository;
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
