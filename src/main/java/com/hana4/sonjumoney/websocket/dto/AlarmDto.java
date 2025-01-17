package com.hana4.sonjumoney.websocket.dto;

public record AlarmDto(
	Long alarmSessionId,
	Long senderId,
	String alarmType
) {
}
