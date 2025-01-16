package com.hana4.sonjumoney.dto.response;

import lombok.Builder;

@Builder
public record AccountResponse(
	Integer code,
	String message
) {
	public static AccountResponse of(Integer code, String message) {
		return AccountResponse.builder()
			.code(code)
			.message(message)
			.build();
	}
}
