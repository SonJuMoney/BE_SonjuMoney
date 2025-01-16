package com.hana4.sonjumoney.dto.response;

import lombok.Builder;

@Builder
public record PinValidResponse(
	Integer code,
	String message
	) {
	public static PinValidResponse of(Integer code, String message){
		return PinValidResponse.builder()
			.code(code)
			.message(message)
			.build();
	}
}
