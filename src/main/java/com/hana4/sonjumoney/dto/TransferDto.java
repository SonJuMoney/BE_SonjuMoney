package com.hana4.sonjumoney.dto;

import com.hana4.sonjumoney.domain.Account;

import lombok.Builder;

@Builder
public record TransferDto(
	Account sender,
	Account receiver,
	Long amount
) {
	public static TransferDto of(
		Account sender,
		Account receiver,
		Long amount
	) {
		return TransferDto.builder()
			.sender(sender)
			.receiver(receiver)
			.amount(amount)
			.build();
	}
}
