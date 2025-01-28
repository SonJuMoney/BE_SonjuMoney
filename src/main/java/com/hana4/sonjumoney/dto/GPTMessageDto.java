package com.hana4.sonjumoney.dto;

import lombok.Builder;

@Builder
public record GPTMessageDto(
	String role,
	String content
) {
	public static GPTMessageDto of(String role, String content) {
		return GPTMessageDto.builder()
			.role(role)
			.content(content)
			.build();
	}
}
