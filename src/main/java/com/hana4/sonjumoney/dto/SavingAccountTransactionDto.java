package com.hana4.sonjumoney.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

@Builder
public record SavingAccountTransactionDto(

	@JsonProperty("created_at")
	LocalDateTime createdAt,

	String message,

	Long amount

) {
	public static SavingAccountTransactionDto of(LocalDateTime createdAt,
		String message,
		Long amount) {
		return SavingAccountTransactionDto.builder()
			.createdAt(createdAt)
			.message(message)
			.amount(amount)
			.build();
	}
}
