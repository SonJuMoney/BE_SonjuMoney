package com.hana4.sonjumoney.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hana4.sonjumoney.dto.JwtTokenDto;
import com.hana4.sonjumoney.dto.request.SignInRequest;
import com.hana4.sonjumoney.service.UserService;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	WebApplicationContext context;

	@Autowired
	private UserService userService;


	private static String accessToken;

	@BeforeAll
	void setUp() {
		try {
			SignInRequest signInRequest = new SignInRequest("test1", "1234");
			String reqBody = objectMapper.writeValueAsString(signInRequest);
			ResultActions resultActions = mockMvc.perform(
					post("/api/auth/sign-in").contentType(MediaType.APPLICATION_JSON).content(reqBody))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
			JwtTokenDto jwtTokenDto = objectMapper.readValue(
				resultActions.andReturn().getResponse().getContentAsString(),JwtTokenDto.class);
			accessToken= jwtTokenDto.access_token();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@AfterAll
	public void cleanUp()throws IOException {

	}

	@Test
	@DisplayName("/user - User info test")
	void getUserInfo()throws Exception {
		String url = "/api/users";
		mockMvc.perform(
				get(url)
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
	}

}