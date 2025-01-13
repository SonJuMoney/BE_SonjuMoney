package com.hana4.sonjumoney.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TransactionType {
	DEPOSIT("입금"),
	WITHDRAW("출금");
	private String value;
}
