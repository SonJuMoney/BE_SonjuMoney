package com.hana4.sonjumoney.dto.response;

import lombok.Builder;

@Builder
public record FeedLikeResponse(
	Integer code,
	String message
) {
	public static FeedLikeResponse of(Integer code, String message) {
		return FeedLikeResponse.builder()
			.code(code)
			.message(message)
			.build();
	}
}
