package com.hana4.sonjumoney.dto.response;

import lombok.Builder;

@Builder
public record DeleteEventResponse(
	Integer code,
	String message
) {
	public static DeleteEventResponse of(Integer code, String message) {
		return DeleteEventResponse.builder()
			.code(code)
			.message(message)
			.build();
	}

}
