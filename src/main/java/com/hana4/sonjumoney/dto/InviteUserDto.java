package com.hana4.sonjumoney.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record InviteUserDto(
	@JsonProperty("user_id")
	Long userId,
	String role
) {
}
