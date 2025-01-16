package com.hana4.sonjumoney.service;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
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
import com.hana4.sonjumoney.dto.request.EventAddRequest;
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

	@Transactional
	public EventResponse addEvent(Long familyId, EventAddRequest eventAddRequest) {
		Family family = familyRepository.findById(familyId)
			.orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_DATA));
		Event event = eventAddRequest.toEntity(family);
		eventRepository.save(event);
		List<Member> members;
		try {
			members = memberRepository.findAllWithUserByIds(eventAddRequest.memberId());
		} catch (NoSuchElementException e) {
			throw new CommonException(ErrorCode.NOT_FOUND_DATA);
		}

		List<EventParticipant> eventParticipants = members.stream()
			.map(member -> EventParticipant.builder()
				.event(event)
				.member(member)
				.build())
			.toList();
		eventParticipantRepository.saveAll(eventParticipants);

		List<EventParticipantResponse> participantResponses = eventParticipants.stream()
			.map(EventParticipantResponse::from)
			.toList();

		return EventResponse.of(
			event.getId(),
			event.getEventCategory(),
			event.getEventName(),
			event.getStartDate(),
			event.getEndDate(),
			participantResponses
		);

	}

	public List<EventResponse> getAllEvents(Long familyId, int getYear, int getMonth) {
		LocalDate startDate = LocalDate.of(getYear, getMonth, 1);
		LocalDate endDate = startDate.with(TemporalAdjusters.lastDayOfMonth());
		List<EventParticipant> participants;
		try {
			participants = eventParticipantRepository.findAllParticipantsByFamilyIdAndEventDateRange(familyId,
				startDate, endDate);
		} catch (NoSuchElementException e) {
			throw new CommonException(ErrorCode.NOT_FOUND_DATA);
		}
		Map<Event, List<EventParticipant>> groupedByEvent = participants.stream()
			.collect(Collectors.groupingBy(EventParticipant::getEvent));

		List<EventResponse> eventResponses = groupedByEvent.entrySet().stream()
			.map(entry -> {
				Event event = entry.getKey();
				List<EventParticipantResponse> participantResponses = entry.getValue().stream()
					.map(EventParticipantResponse::from)
					.toList();

				return EventResponse.of(
					event.getId(),
					event.getEventCategory(),
					event.getEventName(),
					event.getStartDate(),
					event.getEndDate(),
					participantResponses
				);
			})
			.toList();

		return eventResponses;

	}
}
