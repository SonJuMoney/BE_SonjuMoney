package com.hana4.sonjumoney.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ContentExtension {
	JPG("jpeg"),
	JPEG("jpeg"),
	PNG("png"),
	;
	private final String uploadExtension;
}
