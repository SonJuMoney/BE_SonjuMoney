package com.hana4.sonjumoney.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FeedType {
	NORMAL("일반"),
	ALLOWANCE("용돈"),
	THANKS("감사"),
	;
	private final String value;
}
