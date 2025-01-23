package com.hana4.sonjumoney.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

@Builder
public record SavingAccountTransactionDto(
	@JsonProperty("user_name")
	String userName,

	@JsonProperty("created_at")
	LocalDateTime createdAt,

	String message,

	Long amount

) {
	public static SavingAccountTransactionDto of(String userName, LocalDateTime createdAt,
		String message,
		Long amount) {
		return SavingAccountTransactionDto.builder()
			.userName(userName)
			.createdAt(createdAt)
			.message(message)
			.amount(amount)
			.build();
	}
}
