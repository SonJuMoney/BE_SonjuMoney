package com.hana4.sonjumoney.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

@Builder
public record GetChildrenResponse(
	@JsonProperty(value = "user_id")
	Long userId,
	@JsonProperty(value = "user_name")
	String userName) {
	public static GetChildrenResponse of(Long userId, String userName) {
		return GetChildrenResponse.builder()
			.userId(userId)
			.userName(userName)
			.build();
	}
}
