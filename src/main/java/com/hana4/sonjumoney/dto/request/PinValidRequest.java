package com.hana4.sonjumoney.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

@Builder
public record PinValidRequest(
	@JsonProperty(value = "pin", required = true)
	String pin,

	@JsonProperty(value = "mockacc_id", required = true)
	Long mockaccId
) {

}
