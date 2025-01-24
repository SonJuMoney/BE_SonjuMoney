package com.hana4.sonjumoney.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

@Builder
public record CreateAccountRequest(
	@JsonProperty(value = "user_id", required = false)
	Long userId,

	@JsonProperty(value = "mockacc_id", required = true)
	Long mockaccId) {

}
