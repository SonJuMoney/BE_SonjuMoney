package com.hana4.sonjumoney.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

@Builder
public record SavingAccountTransactionDto(

	@JsonProperty("user_name")
	String userName,

	@JsonProperty("profile_link")
	String profileLink,

	@JsonProperty("created_at")
	LocalDateTime createdAt,

	String message,

	Long amount,

	@JsonProperty("after_balance")
	Long afterBalance

) {
	public static SavingAccountTransactionDto of(String userName, String profileLink, LocalDateTime createdAt,
		String message,
		Long amount, Long afterBalance) {
		return SavingAccountTransactionDto.builder()
			.userName(userName)
			.profileLink(profileLink)
			.createdAt(createdAt)
			.message(message)
			.amount(amount)
			.afterBalance(afterBalance)
			.build();
	}
}
