package com.hana4.sonjumoney.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AlarmType {

	ALLOWANCE("용돈","용돈을 보내셨어요"),
	RESPONSE("보내신 용돈의 답장","보내신 용돈의 답장이 왔어요"),
	SAVINGS("적금입금","메시지를 작성해주세요"),
	FEED("피드","새로운 피드가 올라왔어요"),
	EVENT("일정 등록","새로운 일정이 등록되었어요"),
	TRAVEL("여행","여행 일정이 다가오고 있어요"),
	BIRTHDAY("생일","우리 가족의 생일이에요"),
	DINING("외식","가족 외식이 있어요"),
	MEMORIAL("기념일","기념일이에요"),
	OTHERS("기타","가족일정이 있어요"),
	INVITE("초대","가족초대를 요청했어요"),
	;

	private final String value;
	private final String message;
}
