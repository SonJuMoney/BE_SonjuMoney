package com.hana4.sonjumoney.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ContentPrefix {
	ALLOWANCE("allowance/"),
	THANKS("thanks/"),
	FEED("feed/"),
	;
	private final String prefix;
}
