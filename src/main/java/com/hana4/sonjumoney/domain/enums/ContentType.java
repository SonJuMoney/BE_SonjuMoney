package com.hana4.sonjumoney.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ContentType {
	IMAGE("이미지"),
	VIDEO("동영상");
	private final String value;
}
