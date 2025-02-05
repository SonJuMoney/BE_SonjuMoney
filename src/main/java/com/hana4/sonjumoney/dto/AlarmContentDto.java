package com.hana4.sonjumoney.dto;

import java.time.LocalDateTime;

import com.hana4.sonjumoney.domain.enums.AlarmStatus;
import com.hana4.sonjumoney.domain.enums.AlarmType;

import lombok.Builder;

@Builder
public record AlarmContentDto(
	Long alarm_id,
	AlarmStatus status,
	AlarmType alarm_type,
	String message,
	Long link_id,
	LocalDateTime created_at,
	Long child_id
) {
	public static AlarmContentDto of(Long alarm_id, AlarmStatus status, AlarmType alarm_type, String message,
		Long link_id,
		LocalDateTime created_at, Long child_id) {
		return AlarmContentDto.builder()
			.alarm_id(alarm_id)
			.status(status)
			.alarm_type(alarm_type)
			.message(message)
			.link_id(link_id)
			.created_at(created_at)
			.child_id(child_id)
			.build();
	}
}
