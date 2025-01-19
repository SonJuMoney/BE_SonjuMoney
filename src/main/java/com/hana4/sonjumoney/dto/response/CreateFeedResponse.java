package com.hana4.sonjumoney.dto.response;

import lombok.Builder;

@Builder
public record CreateFeedResponse(
	Integer code,
	String message
) {
	public static CreateFeedResponse of(Integer code, String message) {
		return CreateFeedResponse.builder()
			.code(code)
			.message(message)
			.build();
	}
}
