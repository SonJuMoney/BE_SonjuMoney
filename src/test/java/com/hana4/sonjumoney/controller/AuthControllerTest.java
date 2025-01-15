package com.hana4.sonjumoney.controller;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	String authId = "test1";
	String password = "1234";

	@Test
	void getDuplicationTest() throws Exception {
		String url = "/api/auth/id-duplication";
		String notFoundedId = "test0";

		// 아이디가 중복일 때는 true 반환
		mockMvc.perform(
				get(url).contentType(MediaType.APPLICATION_JSON)
					.param("id", authId))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.duplication", is(true)));

		// 없는 아이디를 넣으면 false 반환
		mockMvc.perform(
				get(url).contentType(MediaType.APPLICATION_JSON)
					.param("id", notFoundedId))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.duplication", is(false)));
	}

}
