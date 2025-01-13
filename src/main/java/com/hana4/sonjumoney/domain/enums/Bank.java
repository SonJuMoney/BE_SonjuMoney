package com.hana4.sonjumoney.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Bank {
	HANA("하나");
	private final String value;
}
