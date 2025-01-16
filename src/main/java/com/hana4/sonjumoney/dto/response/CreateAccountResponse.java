package com.hana4.sonjumoney.dto.response;

import lombok.Builder;

@Builder
public record CreateAccountResponse(
	Integer code,
	String message
) {
	public static CreateAccountResponse of(Integer code, String message) {
		return CreateAccountResponse.builder()
			.code(code)
			.message(message)
			.build();
	}
}
