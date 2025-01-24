package com.hana4.sonjumoney.dto.response;

import lombok.Builder;

@Builder
public record DeleteFeedResponse(
	Integer code,
	String message
) {
	public static DeleteFeedResponse of(Integer code, String message) {
		return DeleteFeedResponse.builder()
			.code(code)
			.message(message)
			.build();
		}
}
