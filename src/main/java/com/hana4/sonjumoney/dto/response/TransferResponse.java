package com.hana4.sonjumoney.dto.response;

import lombok.Builder;

@Builder
public record TransferResponse(
	Integer code,
	String message
) {
	public static TransferResponse of(Integer code, String message) {
		return TransferResponse.builder()
			.code(code)
			.message(message)
			.build();
	}
}
