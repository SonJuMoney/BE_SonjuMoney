package com.hana4.sonjumoney.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hana4.sonjumoney.domain.Alarm;
import com.hana4.sonjumoney.domain.enums.AlarmStatus;
import com.hana4.sonjumoney.domain.enums.AlarmType;

import lombok.Builder;

@Builder
public record SendAlarmDto(
	Long alarmId,
	Long alarmSessionId,
	AlarmStatus alarmStatus,
	AlarmType alarmType,
	String message,
	Long linkId,
	LocalDateTime createdAt

) {
	public static SendAlarmDto from(Alarm alarm) {
		return SendAlarmDto.builder()
			.alarmSessionId(alarm.getUser().getId())
			.alarmStatus(alarm.getAlarmStatus())
			.alarmType(alarm.getAlarmType())
			.message(alarm.getMessage())
			.linkId(alarm.getLinkId())
			.createdAt(alarm.getCreatedAt())
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
