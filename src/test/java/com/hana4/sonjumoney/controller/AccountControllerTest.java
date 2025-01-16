package com.hana4.sonjumoney.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import com.hana4.sonjumoney.ControllerTest;
import com.hana4.sonjumoney.dto.request.CreateAccountRequest;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AccountControllerTest extends ControllerTest {

	@Test
	@DisplayName("계좌 등록 테스트")
	void makeAccountTest() throws Exception {
		String api = "/api/accounts";
		CreateAccountRequest request = CreateAccountRequest.builder()
			.mockaccId(1L)
			.userId(2L)
			.build();

		mockMvc.perform(post(api)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.header("Authorization", "Bearer " + accessToken))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("계좌 조회 테스트")
	void getAccountTest()throws Exception {
		String api = "/api/accounts";

		mockMvc.perform(get(api)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + accessToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.account_name").value("하나자유입출금통장"))
			.andExpect(jsonPath("$.bank").value("하나은행"));
	}
}
