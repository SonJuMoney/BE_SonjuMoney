package com.hana4.sonjumoney.service;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.hana4.sonjumoney.dto.GPTMessageDto;
import com.hana4.sonjumoney.dto.request.ChatGPTRequest;
import com.hana4.sonjumoney.dto.response.ChatGPTResponse;
import com.hana4.sonjumoney.exception.CommonException;
import com.hana4.sonjumoney.exception.ErrorCode;

@Service
public class ChatGPTService {

	@Value("${spring.openai.api.key}")
	private String apiKey;

	@Value("${spring.openai.api.url}")
	private String apiUrl;

	@Value("${spring.openai.api.model}")
	private String model;

	public String askChatGPT(String message) {
		RestTemplate restTemplate = new RestTemplate();

		GPTMessageDto messageDto = GPTMessageDto.of("user", message);
		ChatGPTRequest request = ChatGPTRequest.of(model, Collections.singletonList(messageDto));

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(apiKey);

		HttpEntity<ChatGPTRequest> entity = new HttpEntity<>(request, headers);

		ResponseEntity<ChatGPTResponse> response = restTemplate.exchange(
			apiUrl, HttpMethod.POST, entity, ChatGPTResponse.class
		);

		if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
			return response.getBody().choices().get(0).message().content();
		} else {
			throw new CommonException(ErrorCode.GPT_CALL_FAILED);
		}
	}
}
