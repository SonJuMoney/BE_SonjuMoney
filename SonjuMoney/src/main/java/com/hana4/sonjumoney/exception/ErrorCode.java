package com.hana4.sonjumoney.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

	// 404
	NOT_FOUND_USER(40000,HttpStatus.NOT_FOUND,"존재하지 않는 유저입니다."),

	// 500
	INTERNAL_SERVER_ERROR(50000, HttpStatus.INTERNAL_SERVER_ERROR,"서버내부 오류입니다."),
	;
	private final Integer code;
	private final HttpStatus httpStatus;
	private final String message;
}
