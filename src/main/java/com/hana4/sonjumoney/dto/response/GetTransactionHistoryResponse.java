package com.hana4.sonjumoney.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hana4.sonjumoney.domain.enums.TransactionType;

import lombok.Builder;

@Builder
public record GetTransactionHistoryResponse(
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
	public static GetTransactionHistoryResponse of(String userName, String message, TransactionType transactionType,
		Long afterBalance, LocalDateTime createdAt, Long amount) {
		return GetTransactionHistoryResponse.builder()
			.userName(userName)
			.message(message)
			.transactionType(transactionType.getValue())
			.amount(amount)
			.afterBalance(afterBalance)
			.createdAt(createdAt)
			.build();
	}
}
