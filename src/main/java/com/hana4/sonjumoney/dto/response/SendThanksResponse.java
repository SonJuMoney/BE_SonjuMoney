package com.hana4.sonjumoney.dto.response;

import lombok.Builder;

@Builder
public record SendThanksResponse(
	Integer code,
	String message
) {
	public static SendThanksResponse of(Integer code, String message) {
		return SendThanksResponse.builder()
			.code(code)
			.message(message)
			.build();
	}
}
