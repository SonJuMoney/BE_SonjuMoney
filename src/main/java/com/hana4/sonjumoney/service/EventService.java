package com.hana4.sonjumoney.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hana4.sonjumoney.domain.Event;
import com.hana4.sonjumoney.domain.EventParticipant;
import com.hana4.sonjumoney.domain.Family;
import com.hana4.sonjumoney.domain.Member;
import com.hana4.sonjumoney.domain.enums.AlarmType;
import com.hana4.sonjumoney.dto.CreateAlarmDto;
import com.hana4.sonjumoney.dto.request.AddEventRequest;
import com.hana4.sonjumoney.dto.request.UpdateEventRequest;
import com.hana4.sonjumoney.dto.response.DeleteEventResponse;
import com.hana4.sonjumoney.dto.response.EventParticipantResponse;
import com.hana4.sonjumoney.dto.response.EventResponse;
import com.hana4.sonjumoney.exception.CommonException;
import com.hana4.sonjumoney.exception.ErrorCode;
import com.hana4.sonjumoney.repository.EventParticipantRepository;
import com.hana4.sonjumoney.repository.EventRepository;
import com.hana4.sonjumoney.repository.FamilyRepository;
import com.hana4.sonjumoney.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventService {
	private final EventRepository eventRepository;
	private final FamilyRepository familyRepository;
	private final MemberRepository memberRepository;
	private final EventParticipantRepository eventParticipantRepository;
	private final AlarmService alarmService;

	@Transactional
	public EventResponse addEvent(Long userId, Long familyId, AddEventRequest addEventRequest, boolean flag) {
		//userId,familyId기반으로 확인
		Member findMember = memberRepository.findByUserIdAndFamilyId(userId, familyId)
			.orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_MEMBER));

		Family family = findMember.getFamily();
		Event savedEvent = eventRepository.save(addEventRequest.toEntity(family));
		List<Member> members;
		try {
			members = memberRepository.findAllByIds(addEventRequest.memberId());
		} catch (NoSuchElementException e) {
			throw new CommonException(ErrorCode.NOT_FOUND_DATA);
		}

		List<EventParticipant> eventParticipants = members.stream()
			.map(member -> EventParticipant.builder()
				.event(savedEvent)
				.member(member)
				.build())
			.toList();
		eventParticipantRepository.saveAll(eventParticipants);
		if (flag) {
			alarmService.createOneOffAlarm(CreateAlarmDto.of(familyId, findMember.getId(), savedEvent.getId(), familyId,
				AlarmType.EVENT));
		}

		List<EventParticipantResponse> participantResponses = eventParticipants.stream()
			.map(EventParticipantResponse::from)
			.toList();

		return EventResponse.of(
			savedEvent.getId(),
			savedEvent.getEventCategory(),
			savedEvent.getEventName(),
			savedEvent.getStartDateTime(),
			savedEvent.getEndDateTime(),
			savedEvent.getAllDayStatus(),
			participantResponses
		);

	}

	public List<EventResponse> getAllEvents(Long userId, Long familyId, int getYear, int getMonth) {
		validateUserMember(userId, familyId);

		LocalDateTime startDateTime = LocalDateTime.of(getYear, getMonth, 1, 0, 0, 0, 0);
		LocalDateTime endDateTime = startDateTime.with(TemporalAdjusters.lastDayOfMonth())
			.withHour(23)
			.withMinute(59)
			.withSecond(59)
			.withNano(999_999_999);

		List<EventParticipant> participants;
		try {
			participants = eventParticipantRepository.findAllParticipantsByFamilyIdAndEventDateRange(familyId,
				startDateTime, endDateTime);
		} catch (NoSuchElementException e) {
			throw new CommonException(ErrorCode.NOT_FOUND_DATA);
		}
		Map<Event, List<EventParticipant>> groupedParticipants =
			participants.stream()
				.collect(Collectors.groupingBy(EventParticipant::getEvent));

		List<EventResponse> eventResponses = new ArrayList<>();

		groupedParticipants.forEach((event, participantList) -> {
			List<EventParticipantResponse> participantResponses = participantList.stream()
				.map(EventParticipantResponse::from)
				.toList();

			LocalDate eventStartDate = event.getStartDateTime().toLocalDate();
			LocalDate eventEndDate = event.getEndDateTime().toLocalDate();

			LocalDate firstDay = startDateTime.toLocalDate();
			LocalDate lastDay = endDateTime.toLocalDate();
			LocalDate startDate = eventStartDate.isBefore(firstDay) ? firstDay : eventStartDate;
			LocalDate endDate = eventEndDate.isAfter(lastDay) ? lastDay : eventEndDate;

			for (LocalDate currentDate = startDate; !currentDate.isAfter(
				endDate); currentDate = currentDate.plusDays(1)) {
				eventResponses.add(
					EventResponse.ofWithCurrentDate(
						event.getId(),
						event.getEventCategory(),
						event.getEventName(),
						event.getStartDateTime(),
						event.getEndDateTime(),
						currentDate,
						event.getAllDayStatus(),
						participantResponses

					)
				);
			}
		});

		//currentdate 기준 오름차순
		return eventResponses.stream()
			.sorted(Comparator.comparing(EventResponse::currentDate))
			.toList();

	}

	public EventResponse getEvent(Long userId, Long eventId) {

		List<EventParticipant> participants;
		try {
			participants = eventParticipantRepository.findAllParticipantsByEventId(eventId);

		} catch (NoSuchElementException e) {
			throw new CommonException(ErrorCode.NOT_FOUND_DATA);
		}

		Event event = participants.get(0).getEvent();

		validateUserMember(userId, event.getFamily().getId());

		List<EventParticipantResponse> participantResponses = participants.stream()
			.map(EventParticipantResponse::from)
			.toList();

		return EventResponse.of(
			event.getId(),
			event.getEventCategory(),
			event.getEventName(),
			event.getStartDateTime(),
			event.getEndDateTime(),
			event.getAllDayStatus(),
			participantResponses
		);
	}

	@Transactional
	public EventResponse updateEvent(Long userId, Long eventId, UpdateEventRequest updateEventRequest) {
		Event event = eventRepository.findById(eventId)
			.orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_DATA));
		validateUserMember(userId, event.getFamily().getId());
		event.updateEvent(
			updateEventRequest.eventCategory(),
			updateEventRequest.eventName(),
			updateEventRequest.startDateTime(),
			updateEventRequest.endDateTime(),
			updateEventRequest.allDayStatus()
		);
		eventRepository.save(event);

		List<Member> newMembers;
		try {
			newMembers = memberRepository.findAllByIds(updateEventRequest.memberId());
		} catch (NoSuchElementException e) {
			throw new CommonException(ErrorCode.NOT_FOUND_DATA);
		}

		eventParticipantRepository.deleteByEventId(eventId);
		List<EventParticipant> newParticipants = newMembers.stream()
			.map(member -> EventParticipant.builder()
				.event(event)
				.member(member)
				.build())
			.toList();
		eventParticipantRepository.saveAll(newParticipants);

		List<EventParticipantResponse> participantResponses = newParticipants.stream()
			.map(EventParticipantResponse::from)
			.toList();

		return EventResponse.of(
			event.getId(),
			event.getEventCategory(),
			event.getEventName(),
			event.getStartDateTime(),
			event.getEndDateTime(),
			event.getAllDayStatus(),
			participantResponses

		);
	}

	@Transactional
	public DeleteEventResponse deleteEvent(Long userId, Long eventId) {
		Event event = eventRepository.findById(eventId)
			.orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_DATA));
		validateUserMember(userId, event.getFamily().getId());
		eventRepository.delete(event);
		eventParticipantRepository.deleteByEventId(eventId);
		return DeleteEventResponse.of(200, "삭제 성공");
	}

	//사용자확인 메서드
	private void validateUserMember(Long userId, Long familyId) {
		boolean isMember = memberRepository.existsByUserIdAndFamilyId(userId, familyId);
		if (!isMember) {
			throw new CommonException(ErrorCode.UNAUTHORIZED);
		}
	}
}
