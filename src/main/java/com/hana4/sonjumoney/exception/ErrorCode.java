package com.hana4.sonjumoney.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

	//400
	BAD_REQUEST(40000, HttpStatus.BAD_REQUEST, "잘못된 요청 형식입니다."),

	//401
	INVALID_PASSWORD(40101, HttpStatus.UNAUTHORIZED, "비밀번호가 올바르지 않습니다."),
	INVALID_REFRESH_TOKEN(40102, HttpStatus.UNAUTHORIZED, "유효하지 않은 Refresh Token입니다."),
	INVALID_PIN(40103, HttpStatus.UNAUTHORIZED, "PIN 번호가 올바르지 않습니다."),
	INVALID_AUTH_FORMAT(40104, HttpStatus.UNAUTHORIZED, "유효하지 않은 인증 요청 형식입니다."),

	//403
	FORBIDDEN(40300, HttpStatus.FORBIDDEN, "요청 권한이 없습니다."),

	// 404
	NOT_FOUND_USER(40400, HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."),
	NOT_FOUND_DATA(40401, HttpStatus.NOT_FOUND, "해당 데이터를 찾을 수 없습니다."),

	// 500
	INTERNAL_SERVER_ERROR(50000, HttpStatus.INTERNAL_SERVER_ERROR, "서버내부 오류입니다."),
	IMAGE_UPLOAD_ERROR(50001, HttpStatus.INTERNAL_SERVER_ERROR, "이미지 업로드에 실패했습니다.");
	private final Integer code;
	private final HttpStatus httpStatus;
	private final String message;
}
