package org.cookieandkakao.babting.domain.calendar.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Optional;
import org.cookieandkakao.babting.domain.calendar.entity.PersonalCalendar;
import org.cookieandkakao.babting.domain.calendar.repository.PersonalCalendarRepository;
import org.cookieandkakao.babting.domain.member.entity.Member;
import org.cookieandkakao.babting.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class PersonalCalendarServiceTest {

    @InjectMocks
    private PersonalCalendarService personalCalendarService;

    @Mock
    private PersonalCalendarRepository personalCalendarRepository;

    @Mock
    private MemberRepository memberRepository;

    // Mock 객체 초기화
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findOrCreatePersonalCalendarTest_FindExistingPersonalCalendar() {
        // Given
        Long memberId = 1L;
        Member member = new Member(memberId);
        PersonalCalendar existingPersonalCalendar = new PersonalCalendar(member);

        // Mocking
        // Optional.of 는 null이 아닐 때 그 값을 포함하는 Optional 객체 생성
        // null일 경우 NullPointerException을 발생시킴
        given(personalCalendarRepository.findByMemberMemberId(memberId)).willReturn(
            Optional.of(existingPersonalCalendar));

        // When
        PersonalCalendar personalCalendar = personalCalendarService.findOrCreatePersonalCalendar(
            memberId);

        // Then
        verify(personalCalendarRepository).findByMemberMemberId(any(Long.class));
        verify(personalCalendarRepository, times(0)).save(any(PersonalCalendar.class));
        assertNotNull(personalCalendar);
        assertNotNull(personalCalendar.getMember());
    }

    @Test
    void findOrCreatePersonalCalendarTest_CreateNewPersonalCalendar() {
        // Given
        Long memberId = 1L;
        Member member = new Member(memberId);
        PersonalCalendar newPersonalCalendar = new PersonalCalendar(member);

        // Mocking
        given(personalCalendarRepository.findByMemberMemberId(memberId)).willReturn(
            Optional.empty());
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(personalCalendarRepository.save(any(PersonalCalendar.class))).willReturn(
            newPersonalCalendar);

        // When
        PersonalCalendar personalCalendar = personalCalendarService.findOrCreatePersonalCalendar(
            memberId);

        // Then
        verify(personalCalendarRepository).findByMemberMemberId(any(Long.class));
        verify(memberRepository).findById(any(Long.class));
        verify(personalCalendarRepository).save(any(PersonalCalendar.class));
        assertNotNull(personalCalendar);
        assertNotNull(personalCalendar.getMember());
    }

}