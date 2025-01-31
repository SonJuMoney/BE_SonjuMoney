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
			.startDateTime(this.allDayStatus == AllDayStatus.SPECIFIC_TIME ? this.startDateTime :
				this.startDateTime.withHour(0).withMinute(0).withSecond(0).withNano(0))
			.endDateTime(this.allDayStatus == AllDayStatus.SPECIFIC_TIME ? this.endDateTime :
				this.endDateTime.withHour(23).withMinute(59).withSecond(59))
			.allDayStatus(this.allDayStatus)
			.status(NotifyStatus.REGISTERED)
			.build();
	}

	public static AddEventRequest of(EventCategory eventCategory, String eventName, LocalDateTime startDateTime,
		LocalDateTime endDateTime, AllDayStatus allDayStatus, List<Long> memberId) {
		return AddEventRequest.builder()
			.eventCategory(eventCategory)
			.eventName(eventName)
			.startDateTime(startDateTime)
			.endDateTime(endDateTime)
			.allDayStatus(allDayStatus)
			.memberId(memberId)
			.build();
	}
}
