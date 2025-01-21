package com.hana4.sonjumoney.dto;

import com.hana4.sonjumoney.domain.Alarm;
import com.hana4.sonjumoney.domain.enums.AlarmType;

import lombok.Builder;

@Builder
public record SendAlarmDto(
	Long alarmSessionId,
	Long linkId,
	String message,
	AlarmType alarmType
) {
	public static SendAlarmDto from(Alarm alarm) {
		return SendAlarmDto.builder()
			.alarmSessionId(alarm.getUser().getId())
			.linkId(alarm.getLinkId())
			.message(alarm.getMessage())
			.alarmType(alarm.getAlarmType())
			.build();
	}
	public static SendAlarmDto of(Long alarmSessionId, Long linkId, String message,
		AlarmType alarmType) {
		return SendAlarmDto.builder()
			.alarmSessionId(alarmSessionId)
			.linkId(linkId)
			.message(message)
			.alarmType(alarmType)
			.build();
	}
}
