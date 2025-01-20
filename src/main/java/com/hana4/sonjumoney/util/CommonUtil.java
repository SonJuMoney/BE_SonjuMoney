package com.hana4.sonjumoney.util;

import java.time.LocalDateTime;

import com.hana4.sonjumoney.domain.enums.Gender;
import com.hana4.sonjumoney.exception.CommonException;
import com.hana4.sonjumoney.exception.ErrorCode;

public class CommonUtil {
	// 해당 메소드는 주민등록번호 14자리 형식에서만 유효함.
	public static Gender getGender(String residentNum) {
		if (residentNum.length() != 14) {
			throw new CommonException(ErrorCode.BAD_REQUEST);
		}
		char code = residentNum.charAt(7);
		if (code == '1' || code == '3') {
			return Gender.MALE;
		} else if (code == '2' || code == '4') {
			return Gender.FEMALE;
		} else {
			throw new CommonException(ErrorCode.BAD_REQUEST);
		}
	}

	public static LocalDateTime getBirthThisYear(String residentNum) {
		String front = residentNum.substring(0, 6);
		char genderCode = residentNum.charAt(6);
		int year = LocalDateTime.now().getYear();
		int month = Integer.parseInt(front.substring(2, 4));
		int day = Integer.parseInt(front.substring(4, 6));
		if (genderCode == '1' || genderCode == '2') {
			year += 1900;
		} else if (genderCode == '3' || genderCode == '4') {
			year += 2000;
		} else {
			throw new CommonException(ErrorCode.BAD_REQUEST);
		}
		return LocalDateTime.of(year, month, day, 0, 0, 0, 0);
	}
}
