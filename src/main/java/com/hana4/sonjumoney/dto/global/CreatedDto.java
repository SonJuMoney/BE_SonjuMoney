package com.hana4.sonjumoney.dto.global;

import lombok.Builder;

@Builder
public record CreatedDto(
	Integer code,
	String message
) {
	public static CreatedDto of(String message) {
		return CreatedDto.builder()
			.code(201)
			.message(message)
			.build();
	}
}
