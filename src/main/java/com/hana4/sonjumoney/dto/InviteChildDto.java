package com.hana4.sonjumoney.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record InviteChildDto(
	@JsonProperty("user_id")
	Long userId,
	@JsonProperty("user_name")
	String userName
) {
}
