package com.hana4.sonjumoney.dto.response;

import lombok.Builder;

@Builder
public record PostFeedCommentResponse(
	Integer code,
	String message
) {
	public static PostFeedCommentResponse of(Integer code, String message) {
		return PostFeedCommentResponse.builder()
			.code(code)
			.message(message)
			.build();
	}
}
