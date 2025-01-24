package com.hana4.sonjumoney.dto;

import com.hana4.sonjumoney.domain.enums.Gender;

import lombok.Builder;

@Builder
public record JwtTokenDto(
	String access_token,
	String refresh_token,
	Long user_id,
	String user_name,
	String user_profile,
	Gender gender,
	String birth
) {
	public static JwtTokenDto of(String access_token, String refresh_token, Long user_id, String user_name,
		String user_profile, Gender gender, String birth) {
		return JwtTokenDto.builder()
			.access_token(access_token)
			.refresh_token(refresh_token)
			.user_id(user_id)
			.user_name(user_name)
			.user_profile(user_profile)
			.gender(gender)
			.birth(birth)
			.build();
	}
}
