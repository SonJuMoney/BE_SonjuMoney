package com.hana4.sonjumoney.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MemberRole {
	GRANDFATHER("할아버지"),
	GRANDMOTHER("할머니"),
	FATHER("아빠"),
	MOTHER("엄마"),
	SON("아들"),
	DAUGHTER("딸")
	;
	private final String value;
}
