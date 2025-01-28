package com.hana4.sonjumoney.dto.request;

import java.util.List;

import com.hana4.sonjumoney.dto.GPTMessageDto;

import lombok.Builder;

@Builder
public record ChatGPTRequest(
	String model,
	List<GPTMessageDto> messages
) {
	public static ChatGPTRequest of(String model, List<GPTMessageDto> messages) {
		return ChatGPTRequest.builder()
			.model(model)
			.messages(messages)
			.build();
	}
}
