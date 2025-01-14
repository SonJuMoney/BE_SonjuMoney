package com.hana4.sonjumoney.dto;

public record TransactionDto(
	Long senderId,
	Long receiverId,
	Long amount
) {
}
