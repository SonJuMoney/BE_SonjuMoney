package com.hana4.sonjumoney.dto;

import com.hana4.sonjumoney.domain.Account;
import com.hana4.sonjumoney.domain.enums.TransactionType;

import lombok.Builder;

@Builder
public record TransactionHistoryDto(
	Account account,
	Long amount,
	String message,
	Long afterBalance,
	TransactionType transactionType,
	Long opponentAccountId
) {
	public static TransactionHistoryDto of(Account account, Long amount, String message, Long afterBalance,
		TransactionType transactionType, Long opponentAccountId) {
		return TransactionHistoryDto.builder()
			.account(account)
			.amount(amount)
			.message(message)
			.afterBalance(afterBalance)
			.transactionType(transactionType)
			.opponentAccountId(opponentAccountId)
			.build();
	}
}
