package com.hana4.sonjumoney.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Role {
	PARENT("부모"),
	CHILD("자식"),
	INDIVIDUAL("본인");
	private final String value;
}
