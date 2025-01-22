package com.hana4.sonjumoney.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

@Builder
public record GetSavingAccountResponse(

	@JsonProperty("user_name")
	String userName,

	@JsonProperty("profile_link")
	String profileLink,

	@JsonProperty("created_at")
	String createdAt,

	String message,

	Long amount,

	@JsonProperty("after_balance")
	Long afterBalance

) {
	public static GetSavingAccountResponse of(String userName, String profileLink, String createdAt, String message,
		Long amount, Long afterBalance) {
		return GetSavingAccountResponse.builder()
			.userName(userName)
			.profileLink(profileLink)
			.createdAt(createdAt)
			.message(message)
			.amount(amount)
			.afterBalance(afterBalance)
			.build();
	}
}
