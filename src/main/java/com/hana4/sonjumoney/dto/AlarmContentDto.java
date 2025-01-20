package com.hana4.sonjumoney.dto;

import java.time.LocalDateTime;

import com.hana4.sonjumoney.domain.enums.AlarmStatus;

import lombok.Builder;

@Builder
public record AlarmContentDto(
	Long alarm_id,
	AlarmStatus status,
	String message,
	Long link_id,
	LocalDateTime created_at
) {
	public static AlarmContentDto of(Long alarm_id, AlarmStatus status, String message, Long link_id,
		LocalDateTime created_at) {
		return AlarmContentDto.builder()
			.alarm_id(alarm_id)
			.status(status)
			.message(message)
			.link_id(link_id)
			.created_at(created_at)
			.build();
	}
}
