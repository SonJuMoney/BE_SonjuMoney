package com.hana4.sonjumoney.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

@Builder
public record SendAllowanceRequest(
	@JsonProperty(value = "to_id", required = true)
	Long receiverId,

	@JsonProperty(value = "amount", required = true)
	Long amount,

	@JsonProperty("message")
	String message
) {
}
