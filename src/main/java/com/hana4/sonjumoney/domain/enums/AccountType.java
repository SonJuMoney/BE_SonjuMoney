package com.hana4.sonjumoney.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AccountType {
	DEPOSIT("자유입출금"),
	SAVINGS("적금");
	private String value;
}
