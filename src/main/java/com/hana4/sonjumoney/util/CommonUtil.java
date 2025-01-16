package com.hana4.sonjumoney.util;

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
}