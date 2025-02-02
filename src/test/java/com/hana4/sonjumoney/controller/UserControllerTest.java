package com.hana4.sonjumoney.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
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
import org.springframework.mock.web.MockMultipartFile;
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

	private static String accessToken;

	@BeforeAll
	void setUp() {
		try {
			SignInRequest signInRequest = new SignInRequest("test1", "1234");
			String reqBody = objectMapper.writeValueAsString(signInRequest);
			ResultActions resultActions = mockMvc.perform(
					post("/api/v1/auth/sign-in").contentType(MediaType.APPLICATION_JSON).content(reqBody))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
			JwtTokenDto jwtTokenDto = objectMapper.readValue(
				resultActions.andReturn().getResponse().getContentAsString(), JwtTokenDto.class);
			accessToken = jwtTokenDto.access_token();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	@DisplayName("/user - User info test")
	void getUserInfo() throws Exception {
		String url = "/api/v1/users";
		mockMvc.perform(
				get(url)
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("내 아이 조회 테스트")
	void getChildrenTest() throws Exception {
		mockMvc.perform(get("/api/v1/users/children")
				.header("Authorization", "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].user_id").value(3))
			.andExpect(jsonPath("$[0].user_name").value("용돈 받을 애"))
			.andDo(print());
	}

	@Test
	void updateProfileTest() throws Exception {
		MockMultipartFile image = new MockMultipartFile(
			"file",
			"profile-test-image.png",
			MediaType.IMAGE_PNG_VALUE,
			new byte[] {1}
		);

		String api = "/api/v1/users/profiles";
		mockMvc.perform(multipart(api)
				.file(image)
				.with(request -> {
					request.setMethod("PATCH");
					return request;
				})
				.header("Authorization", "Bearer " + accessToken)
				.contentType(MediaType.MULTIPART_FORM_DATA))
			.andExpect(status().isOk());
	}

}
