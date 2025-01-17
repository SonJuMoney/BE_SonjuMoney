package com.hana4.sonjumoney.dto.request;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hana4.sonjumoney.domain.Event;
import com.hana4.sonjumoney.domain.Family;
import com.hana4.sonjumoney.domain.enums.EventCategory;
import com.hana4.sonjumoney.domain.enums.NotifyStatus;

import lombok.Builder;

@Builder
public record AddEventRequest(
	@JsonProperty("event_category")
	EventCategory eventCategory,

	@JsonProperty("event_name")
	String eventName,

	@JsonProperty("start_date")
	LocalDate startDate,

	@JsonProperty("end_date")
	LocalDate endDate,

	@JsonProperty("notify_status")
	NotifyStatus notifyStatus,

	@JsonProperty("event_participants")
	List<Long> memberId) {
	public Event toEntity(Family family) {
		return Event.builder()
			.family(family)
			.eventCategory(this.eventCategory)
			.eventName(this.eventName)
			.startDate(this.startDate)
			.endDate(this.endDate)
			.status(this.notifyStatus)
			.build();
	}

}
