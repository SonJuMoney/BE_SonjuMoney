package com.hana4.sonjumoney.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AlarmStatus {
	RECEIVED("전송됨"),
	CHECKED("확인됨");

	private final String value;
}
