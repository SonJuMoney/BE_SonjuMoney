package com.hana4.sonjumoney.dto.response;

import com.hana4.sonjumoney.domain.enums.Gender;

import lombok.Builder;

@Builder
public record UserInfoResponse(
	String username,
	String userProfile,
	Gender gender,
	String birth
) {
	public static UserInfoResponse of(
		String username,
		String userProfile,
		Gender gender,
		String birth
	) {
		return UserInfoResponse.builder()
			.username(username)
			.userProfile(userProfile)
			.gender(gender)
			.birth(birth)
			.build();
	}
}
