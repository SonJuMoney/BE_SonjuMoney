package com.hana4.sonjumoney.dto.request;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hana4.sonjumoney.domain.Event;
import com.hana4.sonjumoney.domain.Family;
import com.hana4.sonjumoney.domain.enums.AllDayStatus;
import com.hana4.sonjumoney.domain.enums.EventCategory;
import com.hana4.sonjumoney.domain.enums.NotifyStatus;

import lombok.Builder;

@Builder
public record AddEventRequest(
	@JsonProperty("event_category")
	EventCategory eventCategory,

	@JsonProperty("event_name")
	String eventName,

	@JsonProperty("start_date_time")
	LocalDateTime startDateTime,

	@JsonProperty("end_date_time")
	LocalDateTime endDateTime,

	@JsonProperty("all_day_status")
	AllDayStatus allDayStatus,

	@JsonProperty("event_participants")
	List<Long> memberId) {
	public Event toEntity(Family family) {
		return Event.builder()
			.family(family)
			.eventCategory(this.eventCategory)
			.eventName(this.eventName)
			.startDateTime(this.startDateTime)
			.endDateTime(this.endDateTime)
			.allDayStatus(this.allDayStatus)
			.status(NotifyStatus.REGISTERED)
			.build();
	}

}
