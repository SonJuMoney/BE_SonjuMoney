package com.hana4.sonjumoney.websocket.dto;

import lombok.Builder;

@Builder
public record MessageDto(
	String alarmType,
	String message
) {
	public static MessageDto of(String alarmType, String message) {
		return MessageDto.builder()
			.alarmType(alarmType)
			.message(message)
			.build();
	}
}
