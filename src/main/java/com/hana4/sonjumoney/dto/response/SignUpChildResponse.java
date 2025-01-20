package com.hana4.sonjumoney.dto.response;

import lombok.Builder;

@Builder
public record SignUpChildResponse(
	Integer code,
	String message
) {
	public static SignUpChildResponse of(Integer code, String message) {
		return SignUpChildResponse.builder()
			.code(code)
			.message(message)
			.build();
	}
}
