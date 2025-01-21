package com.hana4.sonjumoney.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AlarmType {

	ALLOWANCE("용돈","용돈을 보냈어요.",true),
	THANKS("감사메시지","감사메시지를 보냈어요.",true),
	SAVINGS("적금입금","적금 입금일이에요. 메시지를 작성해주세요.",false),
	FEED("피드","새로운 피드가 올라왔어요.",true),
	EVENT("일정 등록","새로운 일정이 등록됐어요.",true),
	TRAVEL("여행","여행 일정이 다가오고 있어요.",false),
	BIRTHDAY("생일","생일이에요.",false),
	DINING("외식","가족 외식이 있어요.",false),
	MEMORIAL("기념일","기념일이에요.",false),
	OTHERS("일정","가족일정이 있어요.",false),
	INVITE("초대","가족초대를 보냈어요.",true),
	;

	private final String value;
	private final String message;
	private final boolean oneOff;
}
