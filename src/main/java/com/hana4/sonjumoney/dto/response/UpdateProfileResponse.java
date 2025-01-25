package com.hana4.sonjumoney.dto.response;

import lombok.Builder;

@Builder
public record UpdateProfileResponse(
	Integer code,
	String message
) {
	public static UpdateProfileResponse of(Integer code, String message) {
		return UpdateProfileResponse.builder()
			.code(code)
			.message(message)
			.build();
	}
}
