package com.hana4.sonjumoney.dto;

import lombok.Builder;

@Builder
public record JwtTokenDto(
	String access_token,
	String refresh_token
) {
	public static JwtTokenDto of(String access_token, String refresh_token) {
		return JwtTokenDto.builder()
			.access_token(access_token)
			.refresh_token(refresh_token)
			.build();
	}
}
