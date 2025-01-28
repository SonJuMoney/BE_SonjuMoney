package com.hana4.sonjumoney.util;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.hana4.sonjumoney.domain.enums.Gender;
import com.hana4.sonjumoney.domain.enums.MemberRole;
import com.hana4.sonjumoney.exception.CommonException;
import com.hana4.sonjumoney.exception.ErrorCode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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
		log.info(residentNum);
		String front = residentNum.substring(0, 6);
		char genderCode = residentNum.charAt(7);
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

	public static List<MemberRole> getMemberRoles(Integer userBirth, Gender userGender, Integer targetBirth,
		Gender targetGender) {
		List<MemberRole> memberRoles = new ArrayList<MemberRole>();
		if (userBirth > targetBirth) { // user가 더 어림
			if (userGender.equals(Gender.MALE) && targetGender.equals(Gender.MALE)) {
				memberRoles.add(MemberRole.SON);
				memberRoles.add(MemberRole.GRANDFATHER);
			} else if (userGender.equals(Gender.MALE) && targetGender.equals(Gender.FEMALE)) {
				memberRoles.add(MemberRole.SON);
				memberRoles.add(MemberRole.GRANDMOTHER);
			} else if (userGender.equals(Gender.FEMALE) && targetGender.equals(Gender.MALE)) {
				memberRoles.add(MemberRole.DAUGHTER);
				memberRoles.add(MemberRole.GRANDFATHER);
			} else if (userGender.equals(Gender.FEMALE) && targetGender.equals(Gender.FEMALE)) {
				memberRoles.add(MemberRole.DAUGHTER);
				memberRoles.add(MemberRole.GRANDMOTHER);
			}
		} else {
			if (userGender.equals(Gender.MALE) && targetGender.equals(Gender.MALE)) {
				memberRoles.add(MemberRole.GRANDFATHER);
				memberRoles.add(MemberRole.SON);
			} else if (userGender.equals(Gender.MALE) && targetGender.equals(Gender.FEMALE)) {
				memberRoles.add(MemberRole.GRANDMOTHER);
				memberRoles.add(MemberRole.SON);
			} else if (userGender.equals(Gender.FEMALE) && targetGender.equals(Gender.MALE)) {
				memberRoles.add(MemberRole.GRANDFATHER);
				memberRoles.add(MemberRole.DAUGHTER);
			} else if (userGender.equals(Gender.FEMALE) && targetGender.equals(Gender.FEMALE)) {
				memberRoles.add(MemberRole.GRANDMOTHER);
				memberRoles.add(MemberRole.DAUGHTER);
			}
		}
		return memberRoles;
	}

	public static String translateRole(MemberRole memberRole) {
		String role;
		switch (memberRole) {
			case SON -> role = "손자";
			case DAUGHTER -> role = "손녀";
			default -> role = memberRole.getValue();
		}
		return role;
	}

	public static Integer traslateBirth(String residentNum) {
		String year = residentNum.substring(0, 2);
		char code = residentNum.charAt(7);
		if (code == '1' || code == '2') {
			year = "19" + year;
		} else if (code == '3' || code == '4') {
			year = "20" + year;
		} else {
			throw new CommonException(ErrorCode.BAD_REQUEST);
		}
		return Integer.parseInt(year);
	}
}
