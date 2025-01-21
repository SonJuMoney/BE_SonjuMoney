package com.hana4.sonjumoney.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hana4.sonjumoney.dto.request.PinValidRequest;
import com.hana4.sonjumoney.dto.request.SignInRequest;
import com.hana4.sonjumoney.exception.ErrorCode;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MockAccountControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	private static String accessToken;

	@BeforeAll
	void setUp() throws Exception {
		SignInRequest signInRequest = new SignInRequest("test1", "1234");
		MvcResult mvcResult = mockMvc.perform(post("/api/auth/sign-in")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(signInRequest)))
			.andExpect(status().isOk()).andReturn();

		String responseBody = mvcResult.getResponse().getContentAsString();
		Map<String, String> responseMap = objectMapper.readValue(responseBody, Map.class);
		accessToken = responseMap.get("access_token");
	}

	@Test
	@DisplayName("유저의 MockAccounts 목록 조회 테스트")
	void getMockAccountsTest() throws Exception {
		String api = "/api/mock/accounts";
		mockMvc.perform(get(api)
				.header("Authorization", "Bearer " + accessToken))
			.andExpect(status().isOk())
			.andDo(print());
	}

	@Test
	@DisplayName("자녀의 MockAccounts 목록 조회 테스트")
	void getChildMockAccountsTest() throws Exception {
		String api = "/api/mock/accounts";
		mockMvc.perform(get(api).param("user_id", "3")
				.header("Authorization", "Bearer " + accessToken))
			.andExpect(status().isOk())
			.andDo(print());
	}

	@Test
	@DisplayName("Mock계좌 비밀번호 일치 확인 테스트")
	void checkValidMockAccountPin() throws Exception {
		String api = "/api/mock/accounts/pin";
		PinValidRequest request = PinValidRequest.builder()
			.mockaccId(1L)
			.pin("1234")
			.build();

		mockMvc.perform(post(api)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.header("Authorization", "Bearer " + accessToken))
			.andExpect(status().isOk())
			.andDo(print());
	}

	@Test
	@DisplayName("Mock계좌 비밀번호 불일치 확인 테스트")
	void checkInvalidMockAccountPin() throws Exception {
		String api = "/api/mock/accounts/pin";
		PinValidRequest request = PinValidRequest.builder()
			.mockaccId(1L)
			.pin("5000")
			.build();

		mockMvc.perform(post(api)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.header("Authorization", "Bearer " + accessToken))
			.andExpect(status().is(ErrorCode.INVALID_PIN.getHttpStatus().value()))
			.andDo(print());
	}
}
