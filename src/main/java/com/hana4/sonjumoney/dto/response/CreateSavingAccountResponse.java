package com.hana4.sonjumoney.dto.response;

import lombok.Builder;

@Builder
public record CreateSavingAccountResponse(
	Integer code,
	String message) {
	public static CreateSavingAccountResponse of(Integer code, String message) {
		return CreateSavingAccountResponse.builder()
			.code(code)
			.message(message)
			.build();
	}
}
