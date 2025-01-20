package com.hana4.sonjumoney.dto.response;

import lombok.Builder;

@Builder
public record UpdateAlarmResponse(
	Integer code,
	String message
) {
	public static UpdateAlarmResponse of(Integer code, String message) {
		return UpdateAlarmResponse.builder()
			.code(code)
			.message(message)
			.build();
	}
}
