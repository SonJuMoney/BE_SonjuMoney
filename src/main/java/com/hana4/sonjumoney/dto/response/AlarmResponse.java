package com.hana4.sonjumoney.dto.response;

import com.hana4.sonjumoney.dto.AlarmResultDto;

import lombok.Builder;

@Builder
public record AlarmResponse(
	Boolean isSuccess,
	Integer code,
	String message,
	AlarmResultDto result
) {
	public static AlarmResponse of(Boolean isSuccess, Integer code, String message, AlarmResultDto result) {
		return AlarmResponse.builder()
			.isSuccess(isSuccess)
			.code(code)
			.message(message)
			.result(result)
			.build();
	}
}
