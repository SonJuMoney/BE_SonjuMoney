package com.hana4.sonjumoney.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SendAllowanceRequest(
	@JsonProperty(value = "to_id", required = true)
	Long recieverId,

	@JsonProperty(value = "amount", required = true)
	Long amount,

	@JsonProperty("message")
	String message
) {
}
