package com.hana4.sonjumoney.dto.response;

import lombok.Builder;

@Builder
public record ReissueResponse(
	String access_token
) {
	public static ReissueResponse of(
		String access_token
	) {
		return ReissueResponse.builder()
			.access_token(access_token)
			.build();
	}
}
