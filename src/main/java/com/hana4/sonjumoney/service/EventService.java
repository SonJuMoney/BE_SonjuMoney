package com.hana4.sonjumoney.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hana4.sonjumoney.domain.Event;
import com.hana4.sonjumoney.domain.EventParticipant;
import com.hana4.sonjumoney.domain.Family;
import com.hana4.sonjumoney.domain.Member;
import com.hana4.sonjumoney.dto.request.EventAddRequest;
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

	public void addEvent(Long familyId, EventAddRequest eventAddRequest) {
		Family family = familyRepository.findById(familyId)
			.orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_DATA));
		Event event = eventAddRequest.toEntity(family);
		eventRepository.save(event);

		List<Member> members = memberRepository.findAllById(eventAddRequest.eventParticipantsId());
		List<EventParticipant> eventParticipants = members.stream()
			.map(member -> EventParticipant.builder()
				.event(event)
				.member(member)
				.build())
			.toList();
		eventParticipantRepository.saveAll(eventParticipants);
	}
}
