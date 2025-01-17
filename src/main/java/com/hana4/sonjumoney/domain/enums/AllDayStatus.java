package com.hana4.sonjumoney.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AllDayStatus {
	ALL_DAY("하루 종일"),
	SPECIFIC_TIME("특정 시간");
	private final String value;
}
