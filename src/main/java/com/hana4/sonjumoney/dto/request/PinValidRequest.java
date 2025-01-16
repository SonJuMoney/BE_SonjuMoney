package com.hana4.sonjumoney.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

@Builder
public record PinValidRequest(
	@JsonProperty("pin")
	String pin,

	@JsonProperty("mockacc_id")
	Long mockaccId
) {

}
