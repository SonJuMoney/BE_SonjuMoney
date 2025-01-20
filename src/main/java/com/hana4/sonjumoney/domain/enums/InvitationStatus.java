package com.hana4.sonjumoney.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum InvitationStatus {
	ACCEPTED("수락"),
	REJECTED("거절"),
	PENDING("대기");
	private final String value;
}
