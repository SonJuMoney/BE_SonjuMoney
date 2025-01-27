package com.hana4.sonjumoney.dto.response;

import lombok.Builder;

@Builder
public record UpdateProfileResponse(
	Integer code,
	String message,
	String url
) {
	public static UpdateProfileResponse of(Integer code, String message, String url) {
		return UpdateProfileResponse.builder()
			.code(code)
			.message(message)
			.url(url)
			.build();
	}
}
