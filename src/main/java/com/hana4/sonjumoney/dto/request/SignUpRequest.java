package com.hana4.sonjumoney.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SignUpRequest(
	@JsonProperty(value = "auth_id", required = true)
	String authId,
	@JsonProperty(value = "password", required = true)
	String password,
	@JsonProperty(value = "name", required = true)
	String name,
	//주민등록번호를 13자리로 받음.
	@JsonProperty(value = "resident_num", required = true)
	String residentNum,
	@JsonProperty(value = "pin", required = true)
	String pin,
	@JsonProperty(value = "phone", required = true)
	String phone
) {
}
