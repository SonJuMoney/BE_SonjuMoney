package com.hana4.sonjumoney.dto;

import com.hana4.sonjumoney.domain.enums.AlarmType;

import lombok.Builder;

@Builder
public record CreateAlarmDto(
	Long alarmSessionId,
	Long senderId,
	Long linkId,
	Long familyId,
	AlarmType alarmType
) {
	public static CreateAlarmDto of(Long alarmSessionId, Long senderId, Long linkId, Long familyId,
		AlarmType alarmType) {
		return CreateAlarmDto.builder()
			.alarmSessionId(alarmSessionId)
			.senderId(senderId)
			.linkId(linkId)
			.familyId(familyId)
			.alarmType(alarmType)
			.build();
	}
}
