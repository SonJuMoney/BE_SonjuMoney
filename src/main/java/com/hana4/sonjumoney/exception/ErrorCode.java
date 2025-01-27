package com.hana4.sonjumoney.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

	//400
	BAD_REQUEST(40000, HttpStatus.BAD_REQUEST, "잘못된 요청 형식입니다."),
	INSUFFICIENT_BALANCE(40001, HttpStatus.BAD_REQUEST, "잔액이 부족합니다."),
	SAME_ACCOUNT(40002, HttpStatus.BAD_REQUEST, "입, 출금 계좌는 동일할 수 없습니다."),
	ALREADY_EXIST_ACCOUNT(40003, HttpStatus.BAD_REQUEST, "이미 계좌가 등록되어 있습니다."),
	IMPROPER_MEMBER_ROLE(40004, HttpStatus.BAD_REQUEST, "적절하지 않은 멤버역할입니다."),
	DIFFERENT_FAMILY(40005, HttpStatus.BAD_REQUEST, "같은 가족이 아닙니다."),
	BAD_URL_FROM(40006, HttpStatus.BAD_REQUEST, "잘못된 url 형식입니다."),
	WRONG_ALARM_TYPE(40007, HttpStatus.BAD_REQUEST, "알림타입이 잘못되었습니다."),
	WRONG_FILE_NAME(40008, HttpStatus.BAD_REQUEST, "파일 이름은 null일 수 없습니다."),
	WRONG_FILE_TYPE(40009, HttpStatus.BAD_REQUEST, "잘못된 확장자입니다."),
	EXCESSIVE_SIZE(40010, HttpStatus.BAD_REQUEST, "비디오 크기는 10GB를 넘을 수 없습니다."),
	NULL_THANKS_MESSAGE(40011, HttpStatus.BAD_REQUEST, "감사메시지는 null일 수 없습니다."),
	DIFFERENT_MEMBER_USER(40012, HttpStatus.BAD_REQUEST, "유저와 멤버가 다릅니다."),

	//401
	UNAUTHORIZED(40100, HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
	INVALID_PASSWORD(40101, HttpStatus.UNAUTHORIZED, "비밀번호가 올바르지 않습니다."),
	INVALID_REFRESH_TOKEN(40102, HttpStatus.UNAUTHORIZED, "유효하지 않은 Refresh Token입니다."),
	INVALID_PIN(40103, HttpStatus.UNAUTHORIZED, "PIN 번호가 올바르지 않습니다."),
	INVALID_AUTH_FORMAT(40104, HttpStatus.UNAUTHORIZED, "유효하지 않은 인증 요청 형식입니다."),

	//403
	FORBIDDEN(40300, HttpStatus.FORBIDDEN, "요청 권한이 없습니다."),

	// 404
	NOT_FOUND_USER(40400, HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."),
	NOT_FOUND_MEMBER(40401, HttpStatus.NOT_FOUND, "존재하지 않는 멤버입니다."),
	NOT_FOUND_DATA(40402, HttpStatus.NOT_FOUND, "해당 데이터를 찾을 수 없습니다."),
	NOT_FOUND_OPPONENET(40403, HttpStatus.NOT_FOUND, "상대방이 존재하지 않습니다.(개발용 에러)"),

	// 409
	CONFLICT_ID(40901, HttpStatus.CONFLICT, "이미 존재하는 아이디입니다."),
	CONFLICT_USER(40902, HttpStatus.CONFLICT, "이미 존재하는 회원입니다."),
	CONFLICT_INVITATION(40903, HttpStatus.CONFLICT, "이미 수락된 초대입니다."),

	// 500
	INTERNAL_SERVER_ERROR(50000, HttpStatus.INTERNAL_SERVER_ERROR, "서버내부 오류입니다."),
	S3_PROCESS_FAILED(50001, HttpStatus.INTERNAL_SERVER_ERROR, "컨텐츠 작업에 실패했습니다."),
	TRANSACTION_FAILED(50002, HttpStatus.INTERNAL_SERVER_ERROR, "거래가 실패했습니다."),
	ALARM_SEND_FAILED(50003, HttpStatus.INTERNAL_SERVER_ERROR, "알림 전송에 실패했습니다."),
	VIDEO_UPLOAD_FAILED(50004, HttpStatus.INTERNAL_SERVER_ERROR, "비디오 업로드에 실패했습니다."),
	VIDEO_STREAM_FAILED(50005, HttpStatus.INTERNAL_SERVER_ERROR, "비디오 스트리밍 실패했습니다."),
	VIDEO_DELETE_FAILED(50006, HttpStatus.INTERNAL_SERVER_ERROR, "비디오 삭제에 실패했습니다."),
	REDIS_OPERATION_FAILED(50007, HttpStatus.INTERNAL_SERVER_ERROR, "REDIS 작업에 실패했습니다."),

	//503
	REDIS_CONNECTION_FAILED(50300, HttpStatus.SERVICE_UNAVAILABLE, "REDIS 연결에 실패했습니다.");
	private final Integer code;
	private final HttpStatus httpStatus;
	private final String message;
}
