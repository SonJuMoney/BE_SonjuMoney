package com.hana4.sonjumoney.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ContentExtension {
	JPG("jpeg"),
	JPEG("jpeg"),
	PNG("png"),
	MP4("mp4"),
	;
	private final String uploadExtension;
}
