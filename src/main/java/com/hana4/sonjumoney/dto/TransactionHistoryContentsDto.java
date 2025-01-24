package com.hana4.sonjumoney.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hana4.sonjumoney.domain.enums.TransactionType;

import lombok.Builder;

@Builder
public record TransactionHistoryContentsDto(
	@JsonProperty("user_name")
	String userName,

	@JsonProperty("message")
	String message,

	@JsonProperty("transaction_type")
	String transactionType,

	@JsonProperty("after_balance")
	Long afterBalance,

	@JsonProperty("created_at")
	LocalDateTime createdAt,

	@JsonProperty("amount")
	Long amount
) {
	public static TransactionHistoryContentsDto of(String userName, String message, TransactionType transactionType,
		Long afterBalance, LocalDateTime createdAt, Long amount) {
		return TransactionHistoryContentsDto.builder()
			.userName(userName)
			.message(message)
			.transactionType(transactionType.getValue())
			.amount(amount)
			.afterBalance(afterBalance)
			.createdAt(createdAt)
			.build();
	}
}
