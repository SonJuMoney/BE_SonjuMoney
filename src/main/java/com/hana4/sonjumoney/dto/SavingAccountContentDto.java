package com.hana4.sonjumoney.dto;

import java.time.LocalDate;
import java.util.List;

import com.hana4.sonjumoney.dto.response.SavingAccountTransactionDto;

import lombok.Builder;

@Builder
public record SavingAccountContentDto(
	LocalDate date,
	List<SavingAccountTransactionDto> transactions
) {
	public static SavingAccountContentDto of(LocalDate date, List<SavingAccountTransactionDto> transactions) {
		return SavingAccountContentDto.builder()
			.date(date)
			.transactions(transactions)
			.build();
	}
}
