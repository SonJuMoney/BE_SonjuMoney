package com.hana4.sonjumoney.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

@Builder
public record CreateSavingsMessageResponse(
	Integer code,

	@JsonProperty("savings_message")
	String savingsMessage
) {
	public static CreateSavingsMessageResponse of(Integer code, String savingsMessage) {
		return new CreateSavingsMessageResponse(code, savingsMessage);
	}
}
