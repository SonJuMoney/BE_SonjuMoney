package com.hana4.sonjumoney.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hana4.sonjumoney.dto.request.SignInRequest;
import com.hana4.sonjumoney.exception.ErrorCode;
import com.hana4.sonjumoney.service.MockAccountService;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MockAccountControllerTest {

	@Autowired
	private MockAccountService mockAccountService;

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
		mockMvc.perform(get(api).param("user_id", "2")
			.header("Authorization", "Bearer " + accessToken))
			.andExpect(status().isOk())
			.andDo(print());
	}

	@Test
	@DisplayName("Mock계좌 비밀번호 일치 확인 테스트")
	void checkValidMockAccountPin() throws Exception{
		String api = "/api/mock/accounts/pin";
		String pin = "1234";
		String mockAccId = "1";
		mockMvc.perform(post(api)
				.param("pin", pin)
				.param("mockacc_id", mockAccId)
				.header("Authorization", "Bearer " + accessToken))
			.andExpect(status().isOk())
			.andDo(print());
	}

	@Test
	@DisplayName("Mock계좌 비밀번호 불일치 확인 테스트")
	void checkInvalidMockAccountPin() throws Exception{
		String api = "/api/mock/accounts/pin";
		String pin = "5000";
		String mockAccId = "1";
		mockMvc.perform(post(api)
				.param("pin", pin)
				.param("mockacc_id", mockAccId)
				.header("Authorization", "Bearer " + accessToken))
			.andExpect(status().is(ErrorCode.INVALID_PIN.getHttpStatus().value()))
			.andDo(print());
	}
}
