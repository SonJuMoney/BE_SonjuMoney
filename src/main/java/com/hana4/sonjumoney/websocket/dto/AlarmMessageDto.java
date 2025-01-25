package com.hana4.sonjumoney.websocket.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hana4.sonjumoney.domain.enums.AlarmStatus;
import com.hana4.sonjumoney.domain.enums.AlarmType;

import lombok.Builder;

@Builder
public record AlarmMessageDto(
	@JsonProperty("alarm_id")
	Long alarmId,
	@JsonProperty("status")
	AlarmStatus alarmStatus,
	@JsonProperty("alarm_type")
	AlarmType alarmType,
	String message,
	@JsonProperty("link_id")
	Long linkId,
	@JsonProperty("family_id")
	Long familyId,
	@JsonProperty("created_at")
	LocalDateTime createdAt
) {
	public static AlarmMessageDto of(Long alarmId, AlarmStatus alarmStatus, AlarmType alarmType, String message,
		Long linkId, Long familyId, LocalDateTime createdAt) {
		return AlarmMessageDto.builder()
			.alarmId(alarmId)
			.alarmStatus(alarmStatus)
			.alarmType(alarmType)
			.message(message)
			.linkId(linkId)
			.familyId(familyId)
			.createdAt(createdAt)
			.build();
	}
}
