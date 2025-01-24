package com.hana4.sonjumoney.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Builder;

@Builder
public record TransactionHistoryDatesDto(LocalDate date,
										 List<TransactionHistoryContentsDto> contents
) {
	public static TransactionHistoryDatesDto of(LocalDate date, List<TransactionHistoryContentsDto> contents) {
		return TransactionHistoryDatesDto.builder()
			.date(date)
			.contents(contents)
			.build();
	}
}
