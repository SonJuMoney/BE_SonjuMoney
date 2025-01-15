package com.hana4.sonjumoney.dto.response;

import lombok.Builder;

@Builder
public record DuplicationResponse(
	Boolean duplication
) {
	public static DuplicationResponse of(Boolean duplication) {
		return DuplicationResponse.builder()
			.duplication(duplication)
			.build();
	}
}
