package org.cookieandkakao.babting.domain.calendar.service;

import org.cookieandkakao.babting.common.exception.customexception.MemberNotFoundException;
import org.cookieandkakao.babting.domain.calendar.entity.PersonalCalendar;
import org.cookieandkakao.babting.domain.calendar.repository.PersonalCalendarRepository;
import org.cookieandkakao.babting.domain.member.entity.Member;
import org.cookieandkakao.babting.domain.member.repository.MemberRepository;
import org.cookieandkakao.babting.domain.member.service.MemberService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PersonalCalendarService {

    private final PersonalCalendarRepository personalCalendarRepository;
    private final MemberRepository memberRepository;
    private final MemberService memberService;

    public PersonalCalendarService(PersonalCalendarRepository personalCalendarRepository,
        MemberRepository memberRepository, MemberService memberService) {
        this.personalCalendarRepository = personalCalendarRepository;
        this.memberRepository = memberRepository;
        this.memberService = memberService;
    }

    // 개인 캘린더 조회 또는 생성
    @Transactional
    public PersonalCalendar findOrCreatePersonalCalendar(Long memberId) {
        return personalCalendarRepository.findByMemberMemberId(memberId)
            .orElseGet(() -> {
                // 멤버를 먼저 조회
                Member member = memberService.findMember(memberId);

                // 새로운 개인 캘린더 생성
                PersonalCalendar newCalendar = new PersonalCalendar(member);
                return personalCalendarRepository.save(newCalendar);
            });
    }

}
