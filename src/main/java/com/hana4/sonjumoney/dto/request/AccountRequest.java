package com.hana4.sonjumoney.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AccountRequest(
	@JsonProperty(value = "user_id", required = false)
	Long userId,

	@JsonProperty(value = "mockadd_id", required = true)
	Long mockaccId) {

}
