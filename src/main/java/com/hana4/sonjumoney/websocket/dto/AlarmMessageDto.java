package com.hana4.sonjumoney.websocket.dto;

import java.time.LocalDateTime;

import com.hana4.sonjumoney.domain.enums.AlarmStatus;
import com.hana4.sonjumoney.domain.enums.AlarmType;

import lombok.Builder;

@Builder
public record AlarmMessageDto(
	Long alarmId,
	AlarmStatus alarmStatus,
	AlarmType alarmType,
	String message,
	Long linkId,
	LocalDateTime createdAt
) {
	public static AlarmMessageDto of(Long alarmId, AlarmStatus alarmStatus, AlarmType alarmType, String message,
		Long linkId, LocalDateTime createdAt) {
		return AlarmMessageDto.builder()
			.alarmId(alarmId)
			.alarmStatus(alarmStatus)
			.alarmType(alarmType)
			.message(message)
			.linkId(linkId)
			.createdAt(createdAt)
			.build();
	}
}
