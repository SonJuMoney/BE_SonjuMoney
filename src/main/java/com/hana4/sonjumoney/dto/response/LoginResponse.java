package com.hana4.sonjumoney.dto.response;

import lombok.Builder;

@Builder
public record LoginResponse(
	String access_token,
	String refresh_token
) {
	public static LoginResponse of(String access_token, String refresh_token) {
		return LoginResponse.builder()
			.access_token(access_token)
			.refresh_token(refresh_token)
			.build();
	}
}
