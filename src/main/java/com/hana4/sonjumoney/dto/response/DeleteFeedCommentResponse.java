package com.hana4.sonjumoney.dto.response;

import lombok.Builder;

@Builder
public record DeleteFeedCommentResponse(
	Integer code,
	String message
) {
	public static DeleteFeedCommentResponse of(Integer code, String message) {
		return DeleteFeedCommentResponse.builder()
			.code(code)
			.message(message)
			.build();
	}
}
