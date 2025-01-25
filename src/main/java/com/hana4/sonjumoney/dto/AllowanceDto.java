package com.hana4.sonjumoney.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.Builder;

@Builder
public record AllowanceDto(
	Long senderId,
	Long receiverId,
	Long amount,
	String message
) {
	public static AllowanceDto of(Long senderId, Long receiverId, Long amount, String message) {
		return AllowanceDto.builder()
			.senderId(senderId)
			.receiverId(receiverId)
			.amount(amount)
			.message(message)
			.build();
	}
}
