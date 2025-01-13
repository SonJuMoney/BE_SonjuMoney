package com.hana4.sonjumoney.dto.global;

import com.hana4.sonjumoney.exception.ErrorCode;

import lombok.Getter;

@Getter
public class ExceptionDto {
	private final Integer code;
	private final String message;

	public ExceptionDto(ErrorCode errorCode) {
		this.code = errorCode.getCode();
		this.message = errorCode.getMessage();
	}
	public static ExceptionDto of(ErrorCode errorCode) {
		return new ExceptionDto(errorCode);
	}
}
