package org.cookieandkakao.babting.domain.calendar.service;

import org.cookieandkakao.babting.domain.calendar.entity.Event;
import org.cookieandkakao.babting.domain.calendar.entity.Time;
import org.cookieandkakao.babting.domain.calendar.repository.EventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Transactional
    public Event saveAvoidTimeEvent(Time avoidTime) {
        Event avoidTimeEvent = new Event(avoidTime);
        eventRepository.save(avoidTimeEvent);
        return avoidTimeEvent;
    }
}