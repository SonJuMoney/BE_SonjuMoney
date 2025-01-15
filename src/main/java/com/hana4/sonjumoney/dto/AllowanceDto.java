package com.hana4.sonjumoney.dto;

public record AllowanceDto(
	Long senderId,
	Long receiverId,
	Long amount
) {
}
