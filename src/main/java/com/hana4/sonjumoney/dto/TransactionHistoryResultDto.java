package com.hana4.sonjumoney.dto;

import java.util.List;

import lombok.Builder;

@Builder
public record TransactionHistoryResultDto(Boolean hasNext,
										  Integer page,
										  List<TransactionHistoryContentsDto> contents
) {
	public static TransactionHistoryResultDto of(Boolean hasNext, Integer page,
		List<TransactionHistoryContentsDto> contents) {
		return TransactionHistoryResultDto.builder()
			.hasNext(hasNext)
			.page(page)
			.contents(contents)
			.build();
	}
}
