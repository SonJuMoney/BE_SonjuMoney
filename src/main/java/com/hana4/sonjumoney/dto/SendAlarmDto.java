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
	Long familyId,
	Long childId,
	LocalDateTime createdAt

) {
	public static SendAlarmDto from(Alarm alarm,Long childId) {
		return SendAlarmDto.builder()
			.alarmId(alarm.getId())
			.alarmSessionId(alarm.getUser().getId())
			.alarmStatus(alarm.getAlarmStatus())
			.alarmType(alarm.getAlarmType())
			.message(alarm.getMessage())
			.linkId(alarm.getLinkId())
			.familyId(alarm.getFamilyId())
			.childId(childId)
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
