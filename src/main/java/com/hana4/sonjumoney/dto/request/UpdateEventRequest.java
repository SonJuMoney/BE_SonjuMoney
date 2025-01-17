package com.hana4.sonjumoney.dto.request;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hana4.sonjumoney.domain.enums.EventCategory;

public record UpdateEventRequest(
	@JsonProperty("event_category")
	EventCategory eventCategory,

	@JsonProperty("event_name")
	String eventName,

	@JsonProperty("start_date")
	LocalDate startDate,

	@JsonProperty("end_date")
	LocalDate endDate,

	@JsonProperty("event_participants")
	List<Long> memberId) {
}
