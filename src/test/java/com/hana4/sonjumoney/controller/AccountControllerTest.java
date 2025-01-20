package com.hana4.sonjumoney.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import com.hana4.sonjumoney.ControllerTest;
import com.hana4.sonjumoney.dto.request.CreateAccountRequest;
import com.hana4.sonjumoney.dto.request.CreateSavingAccountRequest;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AccountControllerTest extends ControllerTest {

	@Test
	@Transactional
	@DisplayName("계좌 등록 예외 처리 테스트")
	void makeAccountTest() throws Exception {
		String api = "/api/accounts";
		CreateAccountRequest request = CreateAccountRequest.builder()
			.mockaccId(1L)
			.userId(1L)
			.build();

		mockMvc.perform(post(api)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.header("Authorization", "Bearer " + accessToken))
			.andExpect(status().is4xxClientError());
	}

	@Test
	@DisplayName("계좌 조회 테스트")
	void getAccountTest() throws Exception {
		String api = "/api/accounts";

		mockMvc.perform(get(api)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + accessToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.account_name").value("하나자유입출금통장"))
			.andExpect(jsonPath("$.bank").value("하나은행"))
			.andDo(print());
	}

	@Test
	@Transactional
	@DisplayName("적금 계좌 생성시 동일 입출금 계좌 예외 테스트")
	void createSavingAccountTest() throws Exception {
		String api = "/api/accounts/savings";

		CreateSavingAccountRequest request = CreateSavingAccountRequest.builder()
			.message("착하게 자라야 한다~")
			.accountPassword("1234")
			.autoTransferable(true)
			.userId(2L)
			.withdrawalAccountId(1L)
			.depositAccountId(1L)
			.payDay(3)
			.payAmount(50000L)
			.build();

		mockMvc.perform(post(api)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + accessToken)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().is4xxClientError());
	}
}
