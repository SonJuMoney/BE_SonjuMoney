package com.hana4.sonjumoney.dto.response;

import com.hana4.sonjumoney.dto.FeedResultDto;

import lombok.Builder;

@Builder
public record FeedResponse(
	Boolean isSuccess,
	Integer code,
	String message,
	FeedResultDto result
) {
	public static FeedResponse of(Boolean isSuccess, Integer code, String message, FeedResultDto result) {
		return FeedResponse.builder()
			.isSuccess(isSuccess)
			.code(code)
			.message(message)
			.result(result)
			.build();
	}
}
