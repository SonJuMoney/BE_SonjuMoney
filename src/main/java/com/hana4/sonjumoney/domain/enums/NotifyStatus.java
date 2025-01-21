package com.hana4.sonjumoney.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotifyStatus {
	REGISTERED("이벤트등록","등록되었어요."),
	FIRST_NOTIFIED("1차알림","내일은 "),
	SECOND_NOTIFIED("2차알림","오늘은 "),
	COMPLETED("이벤트만료","끝났어요.")
	;
	private final String value;
	private final String message;
}
