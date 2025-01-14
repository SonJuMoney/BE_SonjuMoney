package com.hana4.sonjumoney.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AccountProduct {
	FREE_DEPOSIT("자유입출금"),
	FREE_SAVINGS("자유적금"),
	DREAM_HANA_SAVINGS("꿈하나적금"),
	MY_SAVINGS("내맘적금"),
	;
	private final String name;
}
