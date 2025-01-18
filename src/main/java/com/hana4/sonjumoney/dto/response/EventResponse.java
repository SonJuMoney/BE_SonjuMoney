package com.hana4.sonjumoney.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hana4.sonjumoney.domain.enums.AllDayStatus;
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

	@JsonProperty("start_date_time")
	LocalDateTime startDateTime,

	@JsonProperty("end_date_time")
	LocalDateTime endDateTime,

	@JsonProperty("current_date")
	LocalDate currentDate,

	@JsonProperty("all_day_status")
	String allDayStatus,

	@JsonProperty("event_participants")
	List<EventParticipantResponse> participants) {
	public static EventResponse of(Long eventId, EventCategory eventCategory, String eventName,
		LocalDateTime startDateTime,
		LocalDateTime endDateTime, LocalDate eventDate, AllDayStatus allDayStatus,
		List<EventParticipantResponse> participants) {
		return EventResponse.builder()
			.eventId(eventId)
			.eventCategory(eventCategory)
			.eventName(eventName)
			.startDateTime(startDateTime)
			.endDateTime(endDateTime)
			.eventDate(eventDate)
			.allDayStatus(allDayStatus.getValue())
			.participants(participants)
			.build();
	}
}
