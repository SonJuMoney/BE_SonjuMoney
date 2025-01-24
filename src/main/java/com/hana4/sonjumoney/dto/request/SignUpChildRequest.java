package com.hana4.sonjumoney.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SignUpChildRequest(
	@JsonProperty(value = "auth_id", required = true)
	String authId,
	@JsonProperty(value = "name", required = true)
	String name,
	@JsonProperty(value = "resident_num", required = true)
	String residentNum
) {
}
