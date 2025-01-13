package com.hana4.sonjumoney.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EventCategory {
	TRAVEL("여행"),
	BIRTHDAY("생일"),
	DINING("약속"),
	MEMORIAL("기념일"),
	OTHER("기타")
	;
	private final String value;
}
