package com.hana4.sonjumoney.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Builder;

@Builder
public record TransactionHistoryContentsDto(LocalDate date,
											List<TransactionHistoryTransactionsDto> transactions
) {
	public static TransactionHistoryContentsDto of(LocalDate date,
		List<TransactionHistoryTransactionsDto> transactions) {
		return TransactionHistoryContentsDto.builder()
			.date(date)
			.transactions(transactions)
			.build();
	}
}
