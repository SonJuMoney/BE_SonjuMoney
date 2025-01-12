package com.hana4.sonjumoney.dto.response;

import lombok.Builder;

@Builder
public record UserInfoResponse(
	String username,
	String accountNum
) {
	public static UserInfoResponse of(
		String username,
		String accountNum
	) {
		return UserInfoResponse.builder()
			.username(username)
			.accountNum(accountNum)
			.build();
	}
}
