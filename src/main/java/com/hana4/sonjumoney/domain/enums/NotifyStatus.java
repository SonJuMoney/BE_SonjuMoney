package com.hana4.sonjumoney.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotifyStatus {
	REGISTERED("이벤트등록"),
	FIRST_NOTIFIED("1차알림"),
	SECOND_NOTIFIED("2차알림"),
	COMPLETED("이벤트만료")
	;
	private final String value;
}
