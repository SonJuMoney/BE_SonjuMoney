package com.hana4.sonjumoney.dto;

import com.hana4.sonjumoney.domain.enums.AlarmType;

import lombok.Builder;

@Builder
public record CreateAlarmDto(
	Long alarmSessionId,
	Long senderId,
	Long linkId,
	AlarmType alarmType
) {
	public static CreateAlarmDto of(Long alarmSessionId, Long senderId, Long linkId, AlarmType alarmType) {
		return CreateAlarmDto.builder()
			.alarmSessionId(alarmSessionId)
			.senderId(senderId)
			.linkId(linkId)
			.alarmType(alarmType)
			.build();
	}
}
