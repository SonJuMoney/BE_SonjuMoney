package com.hana4.sonjumoney.dto.request;

import java.time.LocalDate;
import java.util.List;

import com.hana4.sonjumoney.domain.Event;
import com.hana4.sonjumoney.domain.Family;
import com.hana4.sonjumoney.domain.enums.EventCategory;

public record EventAddRequest(EventCategory eventCategory,
							  String eventName, LocalDate startDate, LocalDate endDate,
							  List<Long> eventParticipantsId) {
	public Event toEntity(Family family) {
		return Event.builder()
			.family(family)
			.eventCategory(this.eventCategory)
			.eventName(this.eventName)
			.startDate(this.startDate)
			.endDate(this.endDate)
			.build();
	}

}
