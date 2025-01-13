package com.hana4.sonjumoney.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Gender {
	MALE("남"),
	FEMALE("여"),
	;
	private final String value;
}
