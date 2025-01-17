package com.hana4.sonjumoney.dto;

import java.time.LocalDateTime;

import com.hana4.sonjumoney.domain.enums.AlarmStatus;

public record AlarmContentDto(
	Long alarm_id,
	AlarmStatus status,
	String message,
	Long link_id,
	LocalDateTime created_at
) {
	
}
