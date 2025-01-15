package com.hana4.sonjumoney.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SignUpRequest(
	@JsonProperty(value = "auth_id", required = true)
	String authId,
	@JsonProperty(value = "password", required = true)
	String password,
	@JsonProperty(value = "name", required = true)
	String name,
	@JsonProperty(value = "resident_num", required = true)
	String residentNum,
	@JsonProperty(value = "pin", required = true)
	String pin,
	@JsonProperty(value = "phone", required = true)
	String phone
) {
}
