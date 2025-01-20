package com.hana4.sonjumoney.dto.response;

import lombok.Builder;

@Builder
public record SignUpChildResponse(
	Integer code,
	Long id,
	String message
) {
	public static SignUpChildResponse of(Integer code, Long id, String message) {
		return SignUpChildResponse.builder()
			.code(code)
			.id(id)
			.message(message)
			.build();
	}
}
