package com.hana4.sonjumoney.websocket.dto;

import com.hana4.sonjumoney.domain.enums.AlarmType;

import lombok.Builder;

@Builder
public record AlarmDto(
	Long alarmSessionId,
	Long senderId,
	String alarmType
) {
	public static AlarmDto of(Long alarmSessionId, Long senderId, AlarmType alarmType) {
		return AlarmDto.builder()
			.alarmSessionId(alarmSessionId)
			.senderId(senderId)
			.alarmType(alarmType.getValue())
			.build();
	}
}
