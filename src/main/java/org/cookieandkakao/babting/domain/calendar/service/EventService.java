package org.cookieandkakao.babting.domain.calendar.service;

import org.cookieandkakao.babting.domain.calendar.dto.request.EventCreateRequest;
import org.cookieandkakao.babting.domain.calendar.dto.response.EventGetResponse;
import org.cookieandkakao.babting.domain.calendar.entity.Event;
import org.cookieandkakao.babting.domain.calendar.entity.PersonalCalendar;
import org.cookieandkakao.babting.domain.calendar.entity.Reminder;
import org.cookieandkakao.babting.domain.calendar.entity.Time;
import org.cookieandkakao.babting.domain.calendar.repository.EventRepository;
import org.cookieandkakao.babting.domain.calendar.repository.PersonalCalendarRepository;
import org.cookieandkakao.babting.domain.calendar.repository.ReminderRepository;
import org.cookieandkakao.babting.domain.calendar.repository.TimeRepository;
import org.cookieandkakao.babting.domain.meeting.entity.Location;
import org.cookieandkakao.babting.domain.meeting.repository.LocationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class EventService {

    private final EventRepository eventRepository;
    private final TimeRepository timeRepository;
    private final LocationRepository locationRepository;
    private final ReminderRepository reminderRepository;
    private final PersonalCalendarRepository personalCalendarRepository;
    private final PersonalCalendarService personalCalendarService;


    public EventService(EventRepository eventRepository, TimeRepository timeRepository,
        LocationRepository locationRepository, ReminderRepository reminderRepository,
        PersonalCalendarRepository personalCalendarRepository,
        PersonalCalendarService personalCalendarService) {
        this.eventRepository = eventRepository;
        this.timeRepository = timeRepository;
        this.locationRepository = locationRepository;
        this.reminderRepository = reminderRepository;
        this.personalCalendarRepository = personalCalendarRepository;
        this.personalCalendarService = personalCalendarService;
    }

    @Transactional
    public void saveEvent(EventGetResponse eventGetResponse, Long memberId) {
        // 개인 캘린더 조회 또는 생성
        PersonalCalendar personalCalendar = personalCalendarService.findOrCreatePersonalCalendar(
            memberId);

        //Time 엔티티 저장
        Time time = timeRepository.save(eventGetResponse.time().toEntity());

        //Location 엔티티 저장
        Location location = null;
        if (eventGetResponse.location() != null) {
            location = locationRepository.save(eventGetResponse.location());
        }

        // Event 엔티티 저장
        Event event = new Event(personalCalendar, time, location, eventGetResponse.id(),
            eventGetResponse.title(),
            eventGetResponse.isRecurEvent(),
            eventGetResponse.rrule(), eventGetResponse.dtStart(),
            eventGetResponse.description(),
            eventGetResponse.color(), eventGetResponse.memo());
        eventRepository.save(event);

        // Reminder 저장 (있을 경우)
        if (eventGetResponse.reminders() != null) {
            reminderRepository.save(
                new Reminder(event, eventGetResponse.reminders().getRemindTime()));
        }
    }

    @Transactional
    public void saveCreatedEvent(EventCreateRequest eventCreateRequest, String eventId,
        Long memberId) {
        // 개인 캘린더 조회 또는 생성
        PersonalCalendar personalCalendar = personalCalendarService.findOrCreatePersonalCalendar(
            memberId);

        //Time 엔티티 저장
        Time time = timeRepository.save(eventCreateRequest.time().toEntity());

        // Event 엔티티 저장
        Event event = new Event(personalCalendar, time, null, eventId,
            eventCreateRequest.title(), false, eventCreateRequest.rrule(),
            null, eventCreateRequest.description(), null, null);
        eventRepository.save(event);

    }

}
