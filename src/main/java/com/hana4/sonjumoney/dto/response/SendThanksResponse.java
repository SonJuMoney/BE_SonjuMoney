package com.hana4.sonjumoney.dto.response;

import lombok.Builder;

@Builder
public record SendThanksResponse(
	Integer code,
	String message,
	Long feedId
) {
	public static SendThanksResponse of(Integer code, String message, Long feedId) {
		return SendThanksResponse.builder()
			.code(code)
			.message(message)
			.feedId(feedId)
			.build();
	}
}
