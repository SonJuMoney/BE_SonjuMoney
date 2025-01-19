package com.hana4.sonjumoney.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

@Builder
public record CreateFeedResponse(
	Integer code,
	@JsonProperty(value = "feed_id")
	Long feedId,
	String message
) {
	public static CreateFeedResponse of(Integer code, String message) {
		return CreateFeedResponse.builder()
			.code(code)
			.message(message)
			.build();
	}
}
