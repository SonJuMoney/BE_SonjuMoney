package com.hana4.sonjumoney.dto.response;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hana4.sonjumoney.domain.enums.EventCategory;

import lombok.Builder;

@Builder
public record EventResponse(

	@JsonProperty("event_id")
	Long eventId,

	@JsonProperty("event_category")
	EventCategory eventCategory,

	@JsonProperty("event_name")
	String eventName,

	@JsonProperty("start_date")
	LocalDate startDate,

	@JsonProperty("end_date")
	LocalDate endDate,

	@JsonProperty("event_participants")
	List<EventParticipantResponse> participants) {
	public static EventResponse of(Long eventId, EventCategory eventCategory, String eventName, LocalDate startDate,
		LocalDate endDate, List<EventParticipantResponse> participants) {
		return EventResponse.builder()
			.eventId(eventId)
			.eventCategory(eventCategory)
			.eventName(eventName)
			.startDate(startDate)
			.endDate(endDate)
			.participants(participants)
			.build();
	}
}
