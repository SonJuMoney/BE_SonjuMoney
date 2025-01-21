package com.hana4.sonjumoney.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Range {
	ALL(),
	EXCEPTME(),
	CHILDREN();
}
