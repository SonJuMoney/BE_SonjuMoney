package com.hana4.sonjumoney.dto.response;

import java.util.List;

public record ChatGPTResponse(List<Choice> choices, Usage usage) {
	public record Choice(Message message) {
		public record Message(String role, String content) {
		}
	}

	public record Usage(Integer prompt_tokens, Integer completion_tokens, Integer total_tokens) {
	}
}
