package com.hana4.sonjumoney.dto.response;

import lombok.Builder;

@Builder
public record SignUpResponse(
	Integer code,
	String message
) {
	public static SignUpResponse of(Integer code, String message) {
		return SignUpResponse.builder()
			.code(code)
			.message(message)
			.build();
	}
}
