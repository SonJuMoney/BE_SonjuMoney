package com.hana4.sonjumoney.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AlarmType {

	ALLOWANCE("용돈"),
	RESPONSE("보내신 용돈의 답장"),
	EVENT(""),
	INVITE("초대");

	private final String value;
}
