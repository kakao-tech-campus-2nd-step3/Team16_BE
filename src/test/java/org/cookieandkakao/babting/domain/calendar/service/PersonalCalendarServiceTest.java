package org.cookieandkakao.babting.domain.calendar.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Optional;
import org.cookieandkakao.babting.common.exception.customexception.MemberNotFoundException;
import org.cookieandkakao.babting.domain.calendar.entity.PersonalCalendar;
import org.cookieandkakao.babting.domain.calendar.repository.PersonalCalendarRepository;
import org.cookieandkakao.babting.domain.member.entity.Member;
import org.cookieandkakao.babting.domain.member.service.MemberService;
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
    private MemberService memberService;

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
        assertEquals(member, personalCalendar.getMember());
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
        given(memberService.findMember(memberId)).willReturn(member);
        given(personalCalendarRepository.save(any(PersonalCalendar.class))).willReturn(
            newPersonalCalendar);

        // When
        PersonalCalendar personalCalendar = personalCalendarService.findOrCreatePersonalCalendar(
            memberId);

        // Then
        verify(personalCalendarRepository).findByMemberMemberId(any(Long.class));
        verify(memberService).findMember(any(Long.class));
        verify(personalCalendarRepository).save(any(PersonalCalendar.class));
        assertNotNull(personalCalendar);
        assertNotNull(personalCalendar.getMember());
        assertEquals(member, personalCalendar.getMember());
    }

    @Test
    void findOrCreatePersonalCalendarTest_NotFoundMember() {
        // Given
        Long memberId = 1L;

        // Mocking
        given(memberService.findMember(memberId)).willThrow(
            new MemberNotFoundException("멤버 찾을 수 없음"));

        // When
        Exception e = assertThrows(MemberNotFoundException.class,
            () -> personalCalendarService.findOrCreatePersonalCalendar(memberId));

        // Then
        assertEquals(e.getClass(), MemberNotFoundException.class);
        assertEquals(e.getMessage(), "멤버 찾을 수 없음");
        verify(personalCalendarRepository).findByMemberMemberId(any(Long.class));
        verify(memberService).findMember(any(Long.class));
        verify(personalCalendarRepository, times(0)).save(any(PersonalCalendar.class));
    }

    @Test
    void findOrCreatePersonalCalendarTest_PersonalCalendarSaveFailed() {
        // Given
        Long memberId = 1L;
        Member member = new Member(memberId);

        // Mocking
        given(personalCalendarRepository.findByMemberMemberId(memberId)).willReturn(
            Optional.empty());
        given(memberService.findMember(memberId)).willReturn(member);
        given(personalCalendarRepository.save(any(PersonalCalendar.class))).willThrow(
            new IllegalArgumentException("개인 캘린더 저장 실패"));

        // When
        Exception e = assertThrows(IllegalArgumentException.class,
            () -> personalCalendarService.findOrCreatePersonalCalendar(memberId));

        // Then
        assertEquals(e.getClass(), IllegalArgumentException.class);
        assertEquals(e.getMessage(), "개인 캘린더 저장 실패");
        verify(personalCalendarRepository).findByMemberMemberId(any(Long.class));
        verify(memberService).findMember(any(Long.class));
        verify(personalCalendarRepository).save(any(PersonalCalendar.class));
    }

    @Test
    void findOrCreatePersonalCalendarTest_ErrorFindByMemberMemberId() {
        // Given
        Long memberId = 1L;

        // Mocking
        given(personalCalendarRepository.findByMemberMemberId(memberId)).willThrow(
            new IllegalArgumentException("멤버 id로 찾을 수 없음"));

        // When
        Exception e = assertThrows(IllegalArgumentException.class,
            () -> personalCalendarService.findOrCreatePersonalCalendar(memberId));

        // Then
        assertEquals(e.getClass(), IllegalArgumentException.class);
        assertEquals(e.getMessage(), "멤버 id로 찾을 수 없음");
        verify(personalCalendarRepository).findByMemberMemberId(any(Long.class));
        verify(memberService, times(0)).findMember(any(Long.class));
        verify(personalCalendarRepository, times(0)).save(any(PersonalCalendar.class));
    }
}