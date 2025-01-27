package com.hana4.sonjumoney.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hana4.sonjumoney.domain.enums.TransactionType;

import lombok.Builder;

@Builder
public record TransactionHistoryTransactionsDto(
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
	public static TransactionHistoryTransactionsDto of(String message, TransactionType transactionType,
		Long afterBalance, LocalDateTime createdAt, Long amount) {
		return TransactionHistoryTransactionsDto.builder()
			.message(message)
			.transactionType(transactionType.getValue())
			.amount(amount)
			.afterBalance(afterBalance)
			.createdAt(createdAt)
			.build();
	}
}
