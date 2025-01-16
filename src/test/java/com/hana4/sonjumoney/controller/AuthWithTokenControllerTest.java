package com.hana4.sonjumoney.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import com.hana4.sonjumoney.ControllerTest;
import com.hana4.sonjumoney.dto.request.AuthPinRequest;
import com.hana4.sonjumoney.repository.UserRepository;
import com.hana4.sonjumoney.security.util.JwtUtil;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthWithTokenControllerTest extends ControllerTest {

	@Autowired
	private JwtUtil jwtUtil;
	@Autowired
	private UserRepository userRepository;

	@Test
	void validatePinTest() throws Exception {
		String pin = userRepository.findById(jwtUtil.getUserId(accessToken)).get().getPin();

		String url = "/api/auth/pin";
		AuthPinRequest pinRequest = new AuthPinRequest(pin);
		mockMvc.perform(post(url)
				.header("Authorization", "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(pinRequest)))
			.andExpect(status().isOk());

		AuthPinRequest notMatchPinRequest = new AuthPinRequest("1234567");
		mockMvc.perform(post(url)
				.header("Authorization", "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(notMatchPinRequest)))
			.andExpect(status().is4xxClientError());
	}
}
