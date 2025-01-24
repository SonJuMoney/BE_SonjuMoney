package com.hana4.sonjumoney.dto;

import java.util.List;

import lombok.Builder;

@Builder
public record TransactionHistoryResultDto(Boolean hasNext,
										  Integer page,
										  List<TransactionHistoryDatesDto> dates
) {
	public static TransactionHistoryResultDto of(Boolean hasNext, Integer page,
		List<TransactionHistoryDatesDto> dates) {
		return TransactionHistoryResultDto.builder()
			.hasNext(hasNext)
			.page(page)
			.dates(dates)
			.build();
	}
}
