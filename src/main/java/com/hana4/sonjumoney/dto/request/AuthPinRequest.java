package com.hana4.sonjumoney.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthPinRequest(
	@JsonProperty(value = "pin", required = true)
	String pin
) {
}
